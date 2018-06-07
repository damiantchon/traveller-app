package tim.project.travellerapp.models.request_body_models;

public class PasswordChange {

    private long userId;
    private String oldPassword;
    private String newPassword;

    public long getUserId() {
        return userId;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public PasswordChange(long userId, String oldPassword, String newPassword) {
        this.userId = userId;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
