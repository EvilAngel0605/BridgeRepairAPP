package net.jsrbc.frame.base;

import android.app.IntentService;
import net.jsrbc.frame.handler.client.ClientAnnotationHandler;
import net.jsrbc.frame.handler.database.DatabaseAnnotationHandler;

/**
 * Created by ZZZ on 2017-12-04.
 */
public abstract class BaseService extends IntentService {

    private DatabaseAnnotationHandler databaseAnnotationHandler;

    public BaseService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ClientAnnotationHandler.ready(this).bindClient();
        databaseAnnotationHandler = DatabaseAnnotationHandler.newInstance(this);
        databaseAnnotationHandler.bindMapper();
        created();
    }

    /** 创建完成 */
    protected void created() {}

    @Override
    public void onDestroy() {
        databaseAnnotationHandler.closeDatabase();
        super.onDestroy();
    }
}
