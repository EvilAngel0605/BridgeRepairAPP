package net.jsrbc.frame.handler.client;

import android.content.Context;
import android.util.Log;

import net.jsrbc.R;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.frame.annotation.client.HttpClient;
import net.jsrbc.frame.annotation.client.RequestFile;
import net.jsrbc.frame.annotation.client.RequestMapping;
import net.jsrbc.frame.annotation.client.RequestParam;
import net.jsrbc.frame.enumeration.HttpStatus;
import net.jsrbc.frame.enumeration.RequestMethod;
import net.jsrbc.frame.exception.NonAuthoritativeException;
import net.jsrbc.utils.DataUtils;
import net.jsrbc.utils.FileUtils;
import net.jsrbc.utils.JsonUtils;
import net.jsrbc.utils.StringUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZZZ on 2017-11-30.
 */
public class ClientAnnotationHandler {

    //分界
    private final static String BOUNDARY = "----WebKitFormBoundaryA5qm8VOqMD5xW7MZ";

    private Context context;

    private String serverUrl;

    /** 客户端构造完成 */
    public static ClientAnnotationHandler ready(Context context) {
        return new ClientAnnotationHandler(context);
    }

    /** 绑定客户端程序 */
    public void bindClient() {
        ClientProxy proxy = new ClientProxy();
        for (Field field : context.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(HttpClient.class)) {
                if (field.getType().isInterface()) {
                    try {
                        field.setAccessible(true);
                        field.set(context, Proxy.newProxyInstance(field.getType().getClassLoader(), new Class[]{field.getType()}, proxy));
                    } catch (IllegalAccessException e) {
                        Log.e(getClass().getName(), String.format("%s can not access", field.getName()), e);
                    }
                } else {
                    Log.e(getClass().getName(), String.format("%s should be an interface", field.getType().getName()));
                }
            }
        }
    }

    /** 代理对象 */
    private class ClientProxy implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws IOException,NonAuthoritativeException {
            //检查是否是Object继承下来的方法
            if (method.getDeclaringClass() == Object.class) {
                try {
                    return method.invoke(this, args);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    Log.e(getClass().getName(), "Object origin method exception", e);
                    return null;
                }
            }
            RequestMapping mapping = method.getDeclaredAnnotation(RequestMapping.class);
            //检查是否含有RequestMapping注解
            if (mapping == null) {
                String msg = String.format("%s should be annotated by @RequestMapping", method.getName());
                Log.e(getClass().getName(), msg);
                throw new RuntimeException(msg);
            }
            //根据不同方法获取响应的结果
            ResponseResult responseResult = null;
            switch (mapping.method()) {
                case GET:
                    responseResult = doGet(mapping.path(), convertDoGetParam(method, args));
                    break;
                case POST:
                    responseResult = doPost(mapping.path(), convertDoPostParam(args));
                    break;
                case UPLOAD:
                    responseResult = doUpload(mapping.path(), convertDoUploadParam(method, args));
                    break;
                case DOWNLOAD:
                    responseResult = doDownload(mapping.path(), convertDoGetParam(method, args));
                    break;
            }
            //检查返回的结果
            if (responseResult.getCode() < 200 || responseResult.getCode() >=300) {
                String errorMsg = String.format("http request error, error code is %s, error message is %s", responseResult.getCode(), responseResult.getContent());
                Log.e(getClass().getName(), errorMsg);
                throw new IOException(errorMsg);
            } else if (responseResult.getCode() == HttpStatus.NON_AUTHORITATIVE_INFORMATION.value()) {
                throw new NonAuthoritativeException();
            }
            //注入结果
            Class<?> returnClass = method.getReturnType();
            if (List.class.isAssignableFrom(returnClass)) {
                Type returnType = method.getGenericReturnType();
                if (returnType instanceof ParameterizedType) {
                    return JsonUtils.toList(responseResult.getContent(),(Class<?>) ((ParameterizedType) returnType).getActualTypeArguments()[0]);
                } else {
                    Log.e(getClass().getName(), String.format("wildcard %s is not supported", returnClass.getName()));
                    return null;
                }
            } else {
                return JsonUtils.fromJson(responseResult.getContent(), returnClass);
            }
        }
    }

    /** 处理POST请求 */
    private ResponseResult doPost(String path, Object data) throws IOException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(serverUrl + path + "?tokenId=" + DataUtils.getCurrentToken(context).getId());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(RequestMethod.POST.name());
            conn.setConnectTimeout(10000);
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            try(DataOutputStream os = new DataOutputStream(conn.getOutputStream())) {
                os.writeChars(JsonUtils.toJson(data));
            }
            StringBuilder sb = new StringBuilder();
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String buffer = "";
                while ((buffer = reader.readLine()) != null) {
                    sb.append(buffer);
                }
            }
            return new ResponseResult(conn.getResponseCode(), sb.toString());
        } finally {
            if(conn != null) conn.disconnect();
        }
    }

    /** 处理GET请求 */
    private ResponseResult doGet(String path, HashMap<String, String> args) throws IOException {
        HttpURLConnection conn = null;
        //拼接参数
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(path).append("?").append("tokenId=").append(DataUtils.getCurrentToken(context).getId()).append("&");
        try {
            for (Map.Entry<String, String> en : args.entrySet()) {
                pathBuilder.append(URLEncoder.encode(en.getKey(), StandardCharsets.UTF_8.name())).append("=").append(URLEncoder.encode(en.getValue(), StandardCharsets.UTF_8.name())).append("&");
            }
        }catch (UnsupportedEncodingException e) {
            Log.e(getClass().getName(), "URLEncoder is not support UTF-8", e);
        }
        try {
            URL url = new URL(serverUrl + pathBuilder.deleteCharAt(pathBuilder.length()-1).toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(RequestMethod.GET.name());
            conn.setConnectTimeout(10000);
            conn.setDoInput(true);
            StringBuilder sb = new StringBuilder();
            String buffer = "";
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                while ((buffer = reader.readLine()) != null) {
                    sb.append(buffer);
                }
            }
            return new ResponseResult(conn.getResponseCode(), sb.toString());
        }finally {
            if(conn != null) conn.disconnect();
        }
    }

    /** 处理文件下载 */
    private ResponseResult doDownload(String path, HashMap<String, String> args) throws IOException {
        HttpURLConnection conn = null;
        //拼接参数
        StringBuilder pathBuilder = new StringBuilder();
        pathBuilder.append(path).append("?").append("tokenId=").append(DataUtils.getCurrentToken(context).getId()).append("&");
        try {
            for (Map.Entry<String, String> en : args.entrySet()) {
                pathBuilder.append(URLEncoder.encode(en.getKey(), StandardCharsets.UTF_8.name())).append("=").append(URLEncoder.encode(en.getValue(), StandardCharsets.UTF_8.name())).append("&");
            }
        }catch (UnsupportedEncodingException e) {
            Log.e(getClass().getName(), "URLEncoder is not support UTF-8", e);
        }
        try {
            URL url = new URL(serverUrl + pathBuilder.deleteCharAt(pathBuilder.length()-1).toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(RequestMethod.GET.name());
            conn.setConnectTimeout(10000);
            conn.setDoInput(true);

            File file = new File(AndroidConstant.TEMP_DIRECTORY, "update.apk");
            try(InputStream in = conn.getInputStream();
                FileOutputStream os = new FileOutputStream(file)) {
                int hasRead = 0;
                byte[] buffer = new byte[1024];
                while ((hasRead = in.read(buffer)) > 0) {
                    os.write(buffer, 0, hasRead);
                }
            }
            return new ResponseResult(conn.getResponseCode(), JsonUtils.toJson(file));
        }finally {
            if(conn != null) conn.disconnect();
        }
    }

    /** 处理文件上传 */
    private ResponseResult doUpload(String path, UploadParam uploadParam) throws IOException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(serverUrl + path + "?tokenId=" + DataUtils.getCurrentToken(context).getId());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(RequestMethod.POST.name());
            conn.setConnectTimeout(10000);
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            conn.setRequestProperty("Content-Length", String.valueOf(uploadParam.getBefore().length + uploadParam.getFile().length() + uploadParam.getAfter().length));
            conn.setRequestProperty("HOST", url.getHost());
            conn.setDoOutput(true);
            conn.setDoInput(true);
            try(OutputStream os = conn.getOutputStream();
                FileInputStream fis = new FileInputStream(uploadParam.getFile())) {
                //起始
                os.write(uploadParam.getBefore());
                int hasRead = 0;
                byte[] buffer = new byte[1024];
                while ((hasRead = fis.read(buffer)) > 0) {
                    os.write(buffer, 0, hasRead);
                }
                //结束
                os.write(uploadParam.getAfter());
            }
            StringBuilder sb = new StringBuilder();
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String buffer = "";
                while ((buffer = reader.readLine()) != null) {
                    sb.append(buffer);
                }
            }
            return new ResponseResult(conn.getResponseCode(), sb.toString());
        }finally {
            if (conn != null) conn.disconnect();
        }
    }

    /** 转换POST方法的参数 */
    private Object convertDoPostParam(Object[] args) {
        if (args.length != 1) throw new RuntimeException(String.format("Request method should only keep one param, this method got %s", args.length));
        return args[0];
    }

    /** 转换GET方法的参数 */
    private HashMap<String, String> convertDoGetParam(Method method, Object[] args) {
        HashMap<String, String> map = new HashMap<>();
        Annotation[][] annotations = method.getParameterAnnotations();
        //注解的长度应该和参数的长度保持一致
        String errorMsg = "parameter should be annotated by @RequestParam";
        if (args != null && annotations.length != args.length) {
            Log.e(getClass().getName(), errorMsg);
            throw new RuntimeException(errorMsg);
        }
        //查找对应的参数名和值集合
        for (int i=0; i<annotations.length; i++) {
            //不拼接为null的值
            if (args == null || args[i] == null) continue;
            String paramName = "";
            for (Annotation annotation : annotations[i]) {
                if (annotation instanceof RequestParam) {
                    paramName = ((RequestParam) annotation).value();
                }
            }
            if (StringUtils.isEmpty(paramName)) {
                Log.e(getClass().getName(), errorMsg);
                throw new RuntimeException(errorMsg);
            }
            map.put(paramName, String.valueOf(args[i]));
        }
        return map;
    }

    /** 转换GET方法的参数 */
    private UploadParam convertDoUploadParam(Method method, Object[] args) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        //待上传的文件
        File uploadFile = null;
        //获取参数声明
        Annotation[][] annotations = method.getParameterAnnotations();
        //注解的长度应该和参数的长度保持一致
        String errorMsg = "parameter should be annotated by @RequestParam or @RequestFile";
        if (args != null && annotations.length != args.length) {
            Log.e(getClass().getName(), errorMsg);
            throw new RuntimeException(errorMsg);
        }
        //循环组成上传参数
        for (int i=0; i<annotations.length; i++) {
            //不拼接为null的值
            if (args == null || args[i] == null) continue;
            for (Annotation annotation : annotations[i]) {
                if (annotation instanceof RequestParam) {
                    builder.append("--").append(BOUNDARY).append("\r\n")
                            .append("Content-Disposition: form-data; name=\"").append(((RequestParam) annotation).value()).append("\"\r\n")
                            .append("\r\n").append(String.valueOf(args[i])).append("\r\n");
                } else if (annotation instanceof RequestFile && args[i] instanceof File) {
                    uploadFile = (File) args[i];
                } else {
                    Log.e(getClass().getName(), errorMsg);
                    throw new RuntimeException(errorMsg);
                }
            }
        }
        if (uploadFile == null) {
            errorMsg = "upload file is not found, do you forget to annotate @RequestFile?";
            Log.e(getClass().getName(), errorMsg);
            throw new RuntimeException(errorMsg);
        } else {
            builder.append("--" + BOUNDARY + "\r\n")
                    .append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(uploadFile.getName()).append("\"").append("\r\n")
                    .append("Content-Type: application/octet-stream" + "\r\n")
                    .append("\r\n");
        }
        //返回结果
        byte[] before = builder.toString().getBytes(StandardCharsets.UTF_8);
        byte[] after = ("\r\n--" + BOUNDARY + "--\r\n").getBytes(StandardCharsets.UTF_8);
        return new UploadParam(before, uploadFile, after);
    }

    /** 上传参数 */
    private class UploadParam {

        private byte[] before;

        private File file;

        private byte[] after;

        UploadParam(byte[] before, File file, byte[] after) {
            this.before = before;
            this.file = file;
            this.after = after;
        }

        byte[] getBefore() {
            return before;
        }

        File getFile() {
            return file;
        }

        byte[] getAfter() {
            return after;
        }
    }

    /** Http请求结果 */
    private class ResponseResult {

        /** 状态码 */
        private int code;

        /** 反馈的内容 */
        private String content;

        ResponseResult(int code, String content) {
            this.code = code;
            this.content = content;
        }

        int getCode() {
            return code;
        }

        String getContent() {
            return content;
        }
    }

    private ClientAnnotationHandler(Context context) {
        this.context = context;
        this.serverUrl = context.getString(R.string.server_url);
    }
}
