package hu.rob.waf;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter("/*")
public class RobWafFilter implements Filter {

    // A TomEE beépített JMS erõforrásai – pont úgy, mint a tegnapi MDB-nél!
    @Resource
    private ConnectionFactory connectionFactory;

    @Resource(name = "FeladatQueue")
    private Queue feladatQueue;

    private static final Pattern TAMADAS_MINTA = Pattern.compile(
        "(?i)(union\\s+select|or\\s+1=1|drop\\s+table|--|<script>|javascript:|eval\\()"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println(">>> [ROB-WAF] Alkalmazásszintû tûzfal elindult (JMS/MDB naplózással)!");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String kliensIp = request.getRemoteAddr();
        String uri = request.getRequestURI();
        String query = request.getQueryString() != null ? URLDecoder.decode(request.getQueryString(), "UTF-8") : "";
        String teljesKeres = uri + "?" + query;

        if (TAMADAS_MINTA.matcher(teljesKeres).find()) {
            String riasztas = "[WAF-BLOKKOLVA] IP: " + kliensIp + " | Kérés: " + teljesKeres;
            System.err.println(">>> " + riasztas);

            // Aszinkron elküldés a FeladatQueue-ra -> a RobQueueMdb automatikusan beírja a MySQL-be!
            Connection conn = null;
            Session session = null;
            try {
                conn = connectionFactory.createConnection();
                session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageProducer producer = session.createProducer(feladatQueue);
                TextMessage msg = session.createTextMessage(riasztas);
                producer.send(msg);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (session != null) session.close();
                    if (conn != null) conn.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("text/plain; charset=UTF-8");
            response.getWriter().write("403 FORBIDDEN - A kérést a RobWAF biztonsági szûrõ blokkolta!");
            return;
        }

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
        System.out.println(">>> [ROB-WAF] Alkalmazásszintû tûzfal leállt.");
    }
}