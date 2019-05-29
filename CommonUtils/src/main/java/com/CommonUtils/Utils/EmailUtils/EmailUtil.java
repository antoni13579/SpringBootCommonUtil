package com.CommonUtils.Utils.EmailUtils;

import javax.mail.MessagingException;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.CommonUtils.Utils.ArrayUtils.ArrayUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class EmailUtil 
{
	private EmailUtil() {}
	
	public static void sendSimpleEmailMessage(final JavaMailSender javaMailSender, final String sendFrom, final String topic, final String info, final String... sendTo) 
	{
		try
		{
			SimpleMailMessage message = new SimpleMailMessage();
	        message.setFrom(sendFrom);
	        message.setTo(sendTo);
	        message.setSubject(topic);
	        message.setText(info);
	        javaMailSender.send(message);
		}
		catch (Exception ex)
		{
			StringBuilder sb = new StringBuilder();
			if (!ArrayUtil.isArrayEmpty(sendTo))
			{
				for (int i = 0; i < sendTo.length; i++)
				{
					sb.append(sendTo[i]);
					if (i < sendTo.length - 1)
					{ sb.append(",");}
				}
			}
			
			log.error("邮件发送失败，发送人为{}，标题为{}，内容为{}，接收人为{}，异常原因为：", sendFrom, topic, info, sb.toString(), ex);
		}
	}
	
	public static void sendMimeMessage(final JavaMailSender javaMailSender, final String sendFrom, final String topic, final String info, final String... sendTo)
	{ sendMimeMessage(javaMailSender, sendFrom, topic, info, false, sendTo); }
	
	public static void sendMimeMessage(final JavaMailSender javaMailSender, final String sendFrom, final String topic, final String info, final boolean isHtml, final String... sendTo)
	{
		try
		{
			MimeMessageHelper helper = sendMimeMessage(javaMailSender, sendFrom, topic, sendTo);;
            helper.setText(info, isHtml);
            javaMailSender.send(helper.getMimeMessage());
		}
		catch (Exception ex)
		{
			StringBuilder sb = new StringBuilder();
			if (!ArrayUtil.isArrayEmpty(sendTo))
			{
				for (int i = 0; i < sendTo.length; i++)
				{
					sb.append(sendTo[i]);
					if (i < sendTo.length - 1)
					{ sb.append(",");}
				}
			}
			
			log.error("邮件发送失败，发送人为{}，标题为{}，内容为{}，接收人为{}，异常原因为：", sendFrom, topic, info, sb.toString(), ex);
		}
	}
	
	public static void sendMimeMessage(final JavaMailSender javaMailSender, final String sendFrom, final String topic, final String plainText, final String htmlText, final String... sendTo)
	{
		try
		{
			MimeMessageHelper helper = sendMimeMessage(javaMailSender, sendFrom, topic, sendTo);
            helper.setText(plainText, htmlText);
            javaMailSender.send(helper.getMimeMessage());
		}
		catch (Exception ex)
		{
			StringBuilder sb = new StringBuilder();
			if (!ArrayUtil.isArrayEmpty(sendTo))
			{
				for (int i = 0; i < sendTo.length; i++)
				{
					sb.append(sendTo[i]);
					if (i < sendTo.length - 1)
					{ sb.append(",");}
				}
			}
			
			log.error("邮件发送失败，发送人为{}，标题为{}，内容为{}与{}，接收人为{}，异常原因为：", sendFrom, topic, plainText, htmlText, sb.toString(), ex);
		}
	}
	
	private static MimeMessageHelper sendMimeMessage(final JavaMailSender javaMailSender, final String sendFrom, final String topic, final String... sendTo) throws MessagingException
	{
		MimeMessageHelper helper = new MimeMessageHelper(javaMailSender.createMimeMessage(), true);
		helper.setTo(sendTo);
        helper.setFrom(sendFrom);
        helper.setSubject(topic);
        return helper;
	}
}