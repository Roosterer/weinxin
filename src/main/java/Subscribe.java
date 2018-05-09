import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;

public class Subscribe extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }


    private static String token = "gaokuo";
//    private static String appId = "wx2b525ce27678420b";
//    private static String appSecret = "f8d753ccb321dec73349bd68de7f14cb";
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("################################请求来了################################");
        //获取get参数
        Map<String, String[]> parameterMap = req.getParameterMap();
        Enumeration enu=req.getParameterNames();
        while(enu.hasMoreElements()){
            String paraName=(String)enu.nextElement();
            System.out.println(paraName+": "+req.getParameter(paraName));
        }
        StringBuilder sb = new StringBuilder("");
        // 实际的逻辑是在这里

        System.out.println("服务器验证开始");
        String signature = req.getParameter("signature");
        String timestamp = req.getParameter("timestamp");
        String nonce = req.getParameter("nonce");
        String echostr = req.getParameter("echostr");
//      校验是否来自微信服务器，将token、timestamp、nonce三个参数进行字典序排序 2）将三个参数字符串拼接成一个字符串进行sha1加密 3）开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
        sb.setLength(0);
        String[] strArr = new String[]{token,timestamp,nonce};
        Arrays.sort(strArr);
        for (int i = 0; i < strArr.length; i++) {
            sb.append(strArr[i]);
        }
        String sortStr = sb.toString();
        System.out.println("sortStr:"+sortStr);
        String sha1Str = DigestUtils.sha1Hex(sortStr);
        System.out.println("sha1Hex:"+sha1Str);
        if(sha1Str.equals(signature)){
            System.out.println("服务器验证成功");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter out = resp.getWriter();
            //echostr不为null，此次是微信服务器的认证
            if(echostr != null){
                System.out.println("首次绑定服务器验证成功");
                out.write(echostr);
            }else{
                //接收到订阅者的消息，如果为订阅和取消订阅，则做相应的业务处理，其他情况不做处理
                //解析消息类型

                try {
                    SAXReader saxReader = new SAXReader();
                    // 通过read方法读取一个文件 转换成Document对象
                    Document document = saxReader.read(req.getInputStream());
                    Element root = document.getRootElement();//获取根元素
                    Element event = root.element("Event");
                    if(event != null && ("subscribe".equals(event.getText()) || "unsubscribe".equals(event.getText())) ){
                        System.out.println("收到订阅/取消订阅消息");
                        String ToUserName = root.element("ToUserName")==null?"":root.element("ToUserName").getText();
                        String FromUserName = root.element("FromUserName")==null?"":root.element("FromUserName").getText();
                        String CreateTime = root.element("CreateTime")==null?"":root.element("CreateTime").getText();
                        String MsgType = root.element("MsgType")==null?"":root.element("MsgType").getText();
                        String Event = root.element("Event")==null?"":root.element("Event").getText();
                        String EventKey = root.element("EventKey")==null?"":root.element("EventKey").getText();
                        String Ticket = root.element("Ticket")==null?"":root.element("Ticket").getText();
                        service(ToUserName,FromUserName,CreateTime,MsgType,Event,EventKey,Ticket);
                    }else{
                        System.out.println("不是订阅/取消订阅消息");
                    }
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                //返回空串不做处理
                out.write("");
            }
        }else{
            System.out.println("服务器验证失败");
        }

        System.out.println("################################处理完成################################");
        //实际的逻辑是在这里
    }


    private void service(String ToUserName,String FromUserName,String CreateTime,String MsgType,String Event,String EventKey,String Ticket){
        System.out.println("ToUserName："+ToUserName);
        System.out.println("FromUserName："+FromUserName);
        System.out.println("CreateTime："+CreateTime);
        System.out.println("MsgType："+MsgType);
        System.out.println("Event："+Event);
        System.out.println("EventKey："+EventKey);
        System.out.println("Ticket："+Ticket);
    }

}
