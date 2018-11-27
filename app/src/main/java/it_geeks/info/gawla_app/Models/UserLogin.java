package it_geeks.info.gawla_app.Models;

public class UserLogin {
        private data data ;

        public UserLogin(String action,String email , String password) {
            this.data = new data(action,email,password);
        }

       private class data{
       private String action = "login";
       private request request;

            private data(String action,String email , String password) {
                this.action = action;
                request = new request(email,password);
            }

           private class request{
              private String email;
              private String password;

                private request(String email, String password) {
                    this.email = email;
                    this.password = password;
                }// request
            }
        } //data

}// UserLogin
