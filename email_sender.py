import smtplib
import random
from settings import SMTP_LOGIN, SMTP_PASSWORD


def send_email_verification(email):
    code = ''.join([str(random.choice([1, 2, 3, 4, 5, 6, 7, 8, 9, 0])) for _ in range(6)])

    HOST = "smtp.gmail.com"
    SUBJECT = "Smarttracker Account Verification"
    to = email
    FROM = "smarttracker.gt@gmail.com"
    text = f'''Hello, here is the code: {code} to confirm your account in smarttracker.
If you are not registered in smarttracker or you did not request a confirmation email, please ignore/delete this email.'''''
    BODY = "\r\n".join((
        "From: %s" % FROM,
        "To: %s" % to,
        "Subject: %s" % SUBJECT,
        "",
        text
    ))

    server = smtplib.SMTP(HOST)
    server.starttls()
    server.login('smarttracker.gt@gmail.com', 'thanosgt666ve128')
    server.sendmail(FROM, [to], BODY)
    server.quit()
    return code
