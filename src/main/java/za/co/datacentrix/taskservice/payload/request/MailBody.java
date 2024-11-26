package za.co.datacentrix.taskservice.payload.request;

import lombok.Builder;

@Builder
public record MailBody (String to, String subject, String text){

}
