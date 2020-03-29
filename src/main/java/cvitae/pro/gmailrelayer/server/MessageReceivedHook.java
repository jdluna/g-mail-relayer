/**
 * Copyright [2020] [https://github.com/betler]
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * @author betler
 *
 */
package cvitae.pro.gmailrelayer.server;

import java.util.Properties;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.james.protocols.smtp.MailEnvelope;
import org.apache.james.protocols.smtp.SMTPSession;
import org.apache.james.protocols.smtp.hook.HookResult;
import org.apache.james.protocols.smtp.hook.MessageHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @author betler
 *
 */
public class MessageReceivedHook implements MessageHook {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${relayer.smtp.auth.type}")
	private String jur;

	@Override
	public void init(final Configuration config) throws ConfigurationException {
		// Do nothing. It is not invoked, anyway
	}

	@Override
	public void destroy() {
		// Do nothing
	}

	/**
	 * Implements the {@link MessageHook#onMessage(SMTPSession, MailEnvelope)}
	 * method. Parses incoming message and relays to the configured server.
	 */
	@Override
	public HookResult onMessage(final SMTPSession session, final MailEnvelope mail) {

		System.out.println(this.jur);

		final MimeMessage msg;
		final JavaMailSender sender = this.getJavaMailSender();
		String msgId;

		// System.out.println(this.overrideSender);

		try {
			msg = sender.createMimeMessage(mail.getMessageInputStream());
			this.logger.debug("Parsed mime message {}", msg.getMessageID());
			msgId = msg.getMessageID();
		} catch (final Exception e) {
			// TODO Different error codes for different errors?
			this.logger.error("Error parsing mime message from input", e);
			return this.buildHookResult(451, "Error while processing received message");
		}

		try {
			msg.setFrom("gesconte@c-vitae.pro");
			sender.send(msg);
			this.logger.debug("Sent message {} to {}", msgId, msg.getRecipients(RecipientType.TO));
		} catch (final Exception e) {
			// TODO Different error codes for different errors?
			this.logger.error("Error sending message {}", msgId, e);
			return this.buildHookResult(451, "Error while relaying message");
		}

		// Everything OK
		return HookResult.OK;
	}

	/**
	 * Builds a custom {@link HookResult}
	 *
	 * @param code        SMTP return code
	 * @param description description for the return code
	 * @return
	 */
	private HookResult buildHookResult(final int code, final String description) {
		return HookResult.builder().smtpReturnCode(String.valueOf(code)).smtpDescription(description).build();
	}

	public JavaMailSender getJavaMailSender() {
		final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost("");
		mailSender.setPort(1);

		mailSender.setUsername("");
		mailSender.setPassword("");

		final Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		// props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "true");
		props.put("mail.smtp.auth.mechanisms", "NTLM");
		props.put("mail.smtp.auth.ntlm.domain", "");

		return mailSender;
	}

}