package club.tempvs.user.amqp;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface EmailEventProcessor {

    @Output("email.send")
    MessageChannel send();
}
