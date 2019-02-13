package cn.router.api.debug;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created to :
 *
 * @author WANG
 * @date 2019/2/12
 */
public class WeError {

    public static void errorToast(Context context ,String msg){
        dispatch(context,msg);
    }

    public static void error(String msg){
        dispatch(null,msg);
    }

    private static void dispatch(Context context ,String msg) {
        switch (DebugConstant.debugType) {
            case DebugConstant.LOG:
                log(msg);
                break;
            case DebugConstant.TOAST:
                toast(context,msg);
                break;
            default:
                break;
        }
    }

    private static void log(String msg) {
        Log.e(DebugConstant.TAG, msg);
    }

    private static void toast(Context context,String msg){
        if(null == context){
            log(msg);
            return;
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
