package net.jsrbc.client;

import net.jsrbc.frame.annotation.client.RequestMapping;
import net.jsrbc.frame.enumeration.RequestMethod;
import net.jsrbc.pojo.Result;
import net.jsrbc.pojo.User;

import java.io.IOException;

/**
 * Created by ZZZ on 2017-11-30.
 */
public interface LoginClient {

    /** 登录 */
    @RequestMapping(path = "/sys/user/login", method = RequestMethod.POST)
    Result login(User user) throws IOException;
}
