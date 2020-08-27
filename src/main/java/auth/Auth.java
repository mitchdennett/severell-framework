package {{.Package}}.auth;

import com.mitchdennett.framework.crypto.PasswordUtils;
import com.mitchdennett.framework.http.NeedsRequest;
import {{.Package}}.models.User;
import {{.Package}}.models.query.QUser;


import java.util.List;

public class Auth extends NeedsRequest {

    public boolean login(String username, String password) {
        User user = new QUser().email.equalTo(username).findOne();
        boolean auth = false;
        if(user != null) {
            auth = PasswordUtils.checkPassword(password, user.getPassword());
            if(auth) {
                request.getSession().setAttribute("userid", user.getId());
            }
        }

        return auth;
    }

}