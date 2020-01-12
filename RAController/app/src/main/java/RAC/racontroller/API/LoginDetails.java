package RAC.racontroller.API;

class LoginDetails {
    private String address;
    private String username;
    private String password;

    LoginDetails(String address, String username, String password) {
        this.address = address;
        this.username = username;
        this.password = password;
    }

    String getAddress() {
        return address;
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }
}
