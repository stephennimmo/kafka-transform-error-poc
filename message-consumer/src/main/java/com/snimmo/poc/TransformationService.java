package com.snimmo.poc;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class TransformationService {

    private boolean fail = true;

    public MessageValue transform(MessageValue messageValue) {
        if (Objects.equals(messageValue.getValue1(), "FAIL") && fail) {
            throw new TransformationException();
        }
        return messageValue;
    }

    public void setFail(boolean fail) {
        this.fail = fail;
    }

}
