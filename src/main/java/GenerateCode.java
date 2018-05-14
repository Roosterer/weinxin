import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;

/**
 * 传入scene id，获取二维码ticket
 */
public class GenerateCode {

    private static final int startNum = 1;  //推广二维码场景id，起始值
    private static final int endNum = 50;   //推广二维码场景id，结束值
//    private static final String appId = "wx2b525ce27678420b"; //个人，gaokuo258测试
//    private static final String appSecrete = "f8d753ccb321dec73349bd68de7f14cb";

    private static final String appId = "wxab0450217ec3c9d3";   //lflyes
    private static final String appSecrete = "40e62dc3a7b771a7c77b8235b3911982";
    public static void main(String[] args) {

        //https请求方式: GET    获取token
        //https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET
        StringBuilder sb = new StringBuilder();
        sb.append("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=");
        sb.append(appId);
        sb.append("&secret=");
        sb.append(appSecrete);
        String tokenReqUrl = sb.toString();
        String tokenResStr= httpsRequest(tokenReqUrl,"GET",null);

        //传入参数获取带推广二维码ticket和url
        Gson gson = new Gson();
        HashMap<String,String> map = new HashMap<>();
        map = gson.fromJson(tokenResStr,map.getClass());
        String token = map.get("access_token");
        System.out.println(token);
        //
        String ticketReqUrl = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+token;
        String sceneParam = "{\"action_name\": \"QR_LIMIT_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \"##sceneId##\"}}}";
        //获取范围内的所有ticket和url

        System.out.println("推广者ID    二维码下载地址    二维码url");
        for (int i = startNum; i <= endNum; i++) {
            String sceneId = new String(sceneParam);
            sceneId = sceneId.replace("##sceneId##",i+"");
            //
            String ticketResStr= httpsRequest(ticketReqUrl,"POST",sceneId);
            map = gson.fromJson(ticketResStr,map.getClass());
            String ticket = map.get("ticket");
            String url = map.get("url");
            //
            String ticketUrl = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+ticket;
            System.out.println(i +"    "+ticketUrl+"    "+url);

        }



//        String ticketResStr= httpsRequest(ticketReqUrl,"POST",scene);
//        System.out.println(ticketResStr);

    }

    /*
     * 处理https GET/POST请求
     * 请求地址、请求方法、参数
     * */
    public static String httpsRequest(String requestUrl,String requestMethod,String outputStr){
        StringBuffer buffer=null;
        try{
            //创建SSLContext
            SSLContext sslContext=SSLContext.getInstance("SSL");
            TrustManager[] tm={new MyX509TrustManager()};
            //初始化
            sslContext.init(null, tm, new java.security.SecureRandom());;
            //获取SSLSocketFactory对象
            SSLSocketFactory ssf=sslContext.getSocketFactory();
            URL url=new URL(requestUrl);
            HttpsURLConnection conn=(HttpsURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(requestMethod);
            //设置当前实例使用的SSLSoctetFactory
            conn.setSSLSocketFactory(ssf);
            conn.connect();
            //往服务器端写内容
            if(null!=outputStr){
                OutputStream os=conn.getOutputStream();
                os.write(outputStr.getBytes("utf-8"));
                os.close();
            }

            //读取服务器端返回的内容
            InputStream is=conn.getInputStream();
            InputStreamReader isr=new InputStreamReader(is,"utf-8");
            BufferedReader br=new BufferedReader(isr);
            buffer=new StringBuffer();
            String line=null;
            while((line=br.readLine())!=null){
                buffer.append(line);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return buffer.toString();
    }

}
