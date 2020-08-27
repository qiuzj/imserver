package cn.javaee.im.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

/**
 * 用户认证工具类
 */
public class AuthUtils {

    /** 默认密钥 */
    private static final String TOKEN_KEY = "vzLuUQ8Hy/lpdy29BiOO4ZhF/Xi6nfa9KErR7I1Jvhg=";
    private static final Key tokenKey;

    static  {
        byte[] tokenBytes = Decoders.BASE64.decode(TOKEN_KEY);
        tokenKey = Keys.hmacShaKeyFor(tokenBytes);
    }

    /**
     * 验证JWT令牌. 验证通过后返回用户名
     *
     * @return
     */
    public static String checkToken(String token) {
        return Jwts.parserBuilder().setSigningKey(tokenKey).build().parseClaimsJws(token).getBody().getSubject();
    }

}
