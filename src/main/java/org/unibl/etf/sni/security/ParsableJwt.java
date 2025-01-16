package org.unibl.etf.sni.security;

import javassist.compiler.ast.Symbol;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.Base64;
import java.util.Objects;

public class ParsableJwt {
    private String token;
    private ParsableJwt.Header header;
    private ParsableJwt.Payload payload;

    public class Header {
        private String alg;

        public Header() {}

        public Header(String alg) {
            this.alg = alg;
        }

        public String getAlg() {
            return alg;
        }

        public void setAlg(String alg) {
            this.alg = alg;
        }

        public Header fromHeaderString(String decodedHeaderString) throws JSONException {
            JSONObject jsonObject = new JSONObject(decodedHeaderString);

            try {
                return new Header(jsonObject.getString("alg"));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public String toString() {
            return "Header{" +
                    "alg='" + alg + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Header header = (Header) o;
            return Objects.equals(alg, header.alg);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(alg);
        }
    }

    public class Payload {
        private String role;
        private String sub;
        private Long iat;
        private Long exp;

        // extra claims
        private String ip;
        private String userAgent;

        public Payload() {}
        public Payload(String role, String sub, Long iat, Long exp) {
            this.role = role;
            this.sub = sub;
            this.iat = iat;
            this.exp = exp;
        }

        public Payload(String role, String sub, Long iat, Long exp, String ip, String userAgent) {
            this.role = role;
            this.sub = sub;
            this.iat = iat;
            this.exp = exp;

            this.ip = ip;
            this.userAgent = userAgent;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getSub() {
            return sub;
        }

        public void setSub(String sub) {
            this.sub = sub;
        }

        public Long getIat() {
            return iat;
        }

        public void setIat(Long iat) {
            this.iat = iat;
        }

        public Long getExp() {
            return exp;
        }

        public void setExp(Long exp) {
            this.exp = exp;
        }

        public Payload fromPayloadString(String decodedPayloadString) throws JSONException {
            JSONObject jsonObject = new JSONObject(decodedPayloadString);

            try {
                try {
                    String ip = null, userAgent = null;

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                if (jsonObject.has("ip") || jsonObject.has("userAgent")) {
                    ip = jsonObject.getString("ip");
                    userAgent = jsonObject.getString("userAgent");
                }

                return new Payload(
                        jsonObject.getString("role"),
                        jsonObject.getString("sub"),
                        jsonObject.getLong("iat"),
                        jsonObject.getLong("exp"),
                        ip,
                        userAgent
                );
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public String toString() {
            return "Payload{" +
                    "role='" + role + '\'' +
                    ", sub='" + sub + '\'' +
                    ", iat=" + iat +
                    ", exp=" + exp +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Payload payload = (Payload) o;

            boolean flag = true;
            if (ip != null)
                flag = Objects.equals(ip, payload.ip);

            if (userAgent != null)
                flag = Objects.equals(userAgent, payload.userAgent);

            return flag &&
                    Objects.equals(role, payload.role) &&
                    Objects.equals(sub, payload.sub) &&
                    Objects.equals(iat, payload.iat) &&
                    Objects.equals(exp, payload.exp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(role, sub, iat, exp);
        }
    }

    public ParsableJwt(String token) {
        this.token = token;

        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();

        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));

        try {
            this.header = new ParsableJwt.Header();
            this.header = this.header.fromHeaderString(header);

            this.payload = new ParsableJwt.Payload();
            this.payload = this.payload.fromPayloadString(payload);
        } catch (Exception e) {
            e.printStackTrace();
            this.header = null;
            this.payload = null;
        }
    }

    @Override
    public String toString() {
        return "ParsableJwt{" +
                "token='" + token + '\'' +
                ", header=" + header +
                ", payload=" + payload +
                '}';
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParsableJwt that = (ParsableJwt) o;
        return Objects.equals(token, that.token) && Objects.equals(header, that.header) && Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, header, payload);
    }

    public void invalidate() {
        this.payload.setExp(0L);
    }

    public void extend() {
        // extend by 30mins
        this.payload.setExp(this.payload.getExp() + 1800L);
    }

    public boolean isExpired() {
        return this.payload.getExp() < System.currentTimeMillis() / 1000;
    }
}
