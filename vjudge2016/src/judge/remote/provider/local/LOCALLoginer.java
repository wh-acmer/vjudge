package judge.remote.provider.local;

import judge.httpclient.DedicatedHttpClient;
import judge.httpclient.SimpleHttpResponse;
import judge.httpclient.SimpleHttpResponseValidator;
import judge.httpclient.SimpleNameValueEntityFactory;
import judge.remote.RemoteOjInfo;
import judge.remote.account.RemoteAccount;
import judge.remote.loginer.RetentiveLoginer;

import org.apache.commons.lang3.Validate;
import org.apache.http.HttpEntity;
import org.springframework.stereotype.Component;

@Component
public class LOCALLoginer extends RetentiveLoginer {

    @Override
    public RemoteOjInfo getOjInfo() {
        return LOCALInfo.INFO;
    }

    @Override
    protected void loginEnforce(RemoteAccount account, DedicatedHttpClient client) {
        if (client.get(LOCALInfo.path).getBody().contains("<a href=logout.php>")) {
            return;
        }

        HttpEntity entity = SimpleNameValueEntityFactory.create( //
                "user_id", account.getAccountId(), //
                "password", account.getPassword());
        client.get(LOCALInfo.path+"/setlang.php?lang=en");
        client.post(LOCALInfo.path+"/login.php", entity, new SimpleHttpResponseValidator() {
            @Override
            public void validate(SimpleHttpResponse response) throws Exception {
                Validate.isTrue(response.getBody().contains("history.go(-2)"));
            }
        });
    }

}
