import org.apache.commons.codec.digest.DigestUtils;

public class TestMain {

    public static void main(String[] args) {
            String abc = "sss";
            String sha1Str = DigestUtils.sha1Hex(abc);
            System.out.println("==sha1Hex=="+sha1Str);

    }

}
