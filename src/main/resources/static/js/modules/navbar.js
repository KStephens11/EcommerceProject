export class Navbar {

    constructor() {
        this.init();
    }

    init() {
        this.bindevents();
        this.setUsername();
    }

    bindevents() {

    }

    setUsername() {
        $.get("/api/users/me", function(username) {
            $("#usernameDisplay").text(username);
        });
    }


}