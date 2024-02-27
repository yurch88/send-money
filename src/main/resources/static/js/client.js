/**
 * Created by stephan on 20.03.16.
 */

$(function () {
   // VARIABLES =============================================================
   var TOKEN_KEY = "jwtToken"
   var $notLoggedIn = $("#notLoggedIn");
   var $loggedIn = $("#loggedIn").hide();
   var $response = $("#response");
   var $login = $("#login");
   var $userInfo = $("#userInfo").hide();

   // FUNCTIONS =============================================================
   function getJwtToken() {
      return localStorage.getItem(TOKEN_KEY);
   }

   function setJwtToken(token) {
      localStorage.setItem(TOKEN_KEY, token);
   }

   function removeJwtToken() {
      localStorage.removeItem(TOKEN_KEY);
   }

   function doLogin(loginData) {
      $.ajax({
         url: "/api/authenticate",
         type: "POST",
         data: JSON.stringify(loginData),
         contentType: "application/json; charset=utf-8",
         dataType: "json",
         success: function (data, textStatus, jqXHR) {
            setJwtToken(data.id_token);
            $login.hide();
            $notLoggedIn.hide();
            //            showTokenInformation();
            showUserInformation();
         },
         error: function (jqXHR, textStatus, errorThrown) {
            if (jqXHR.status === 401) {
               $('#loginErrorModal')
                  .modal("show")
                  .find(".modal-body")
                  .empty()
                  .html("<p>" + jqXHR.responseJSON.message + "</p>");
            } else {
               throw new Error("an unexpected error occured: " + errorThrown);
            }
         }
      });
   }

   function doLogout() {
      removeJwtToken();
      $login.show();
      $userInfo
         .hide()
         .find("#userInfoBody").empty();
      $loggedIn
         .hide()
         .attr("title", "")
         .empty();
      $notLoggedIn.show();
   }

   function createAuthorizationTokenHeader() {
      var token = getJwtToken();
      if (token) {
         return { "Authorization": "Bearer " + token };
      } else {
         return {};
      }
   }

   function showUserInformation() {
      $.ajax({
         url: "/api/user/get",
         type: "GET",
         contentType: "application/json; charset=utf-8",
         dataType: "json",
         headers: createAuthorizationTokenHeader(),
         success: function (data, textStatus, jqXHR) {
            var $userInfoBody = $userInfo.find("#userInfoBody");
            $userInfoBody.append($(`<h1>Hello ${data.firstname}</h1>`));

            $userInfoBody.append($("<div>").text("Username: " + data.username));
            $userInfoBody.append($("<div>").text("Firstname: " + data.firstname));
            $userInfoBody.append($("<div>").text("Lastname: " + data.lastname));
            $userInfoBody.append($("<div>").text("Email: " + data.email));
            $userInfoBody.append($("<div>").text("Phone Number: " + data.phonenumber));
            $userInfoBody.append($("<div>").text("Amount: " + data.amount));

            var $authorityList = $("<ul>");
            data.authorities.forEach(function (authorityItem) {
               $authorityList.append($("<li>").text(authorityItem.name));
            });
            var $authorities = $("<div>").text("Authorities:");
            $authorities.append($authorityList);
            $userInfoBody.append($authorities);

            var $form = $('<form id="sendMoneyForm" class="panel panel-default panel-body"></form>');
            var $formGroup1 = $('<div class="form-group"></div>');
            var $formGroup2 = $('<div class="form-group"></div>');

            $form.append($("<label>").text("Amount: "));
            $formGroup1.append($("<input>").attr("type", "number").attr("class", "form-control").attr("name", "amount").attr("required", true));
            $form.append($formGroup1);

            $form.append($("<label>").text("Phone Number: "));
            $formGroup2.append($("<input>").attr("type", "tel").attr("class", "form-control").attr("name", "phoneNumber").attr("required", true));
            $form.append($formGroup2);

            $form.append($("<button>").attr("type", "submit").attr("class", "btn btn-default").text("send"));
            $userInfoBody.append($form);
            $form.append("<br />");

            $("#sendMoneyForm").submit(function (event) {
               event.preventDefault();

               var $form = document.getElementById('sendMoneyForm');
               var formData = {
                  amount: $form.getElementsByTagName('input').namedItem('amount').value,
                  phoneNumber: $form.getElementsByTagName('input').namedItem('phoneNumber').value
               };
               doSendMoney(formData);
            });

            $userInfo.show();
         }
      });
   }



   function doSendMoney(sendMoneyData) {
      $.ajax({
         url: "/api/money/send",
         type: "POST",
         headers: createAuthorizationTokenHeader(),
         data: JSON.stringify(sendMoneyData),
         contentType: "application/json; charset=utf-8",
         dataType: "json",
         success: function (data, textStatus, jqXHR) {
            setJwtToken(data.id_token);
            var $userInfoBody = $userInfo.find("#userInfoBody");
            var $form = $('<form id="confirmForm" class="panel panel-default panel-body"></form>');
            var $formGroup = $('<div class="form-group"></div>');
            $form.append($("<label>").text("OTP: "));
            $formGroup.append($("<input>").attr("type", "text").attr("class", "form-control").attr("name", "otp").attr("required", true));
            $form.append($formGroup);
            $form.append($("<button>").attr("type", "submit").attr("class", "btn btn-default").text("send"));
            $userInfoBody.append($form);

            $("#confirmForm").submit(function (event) {
               event.preventDefault();
               var $form = document.getElementById('confirmForm');
               var formData = {
                  otp: $form.getElementsByTagName('input').namedItem('otp').value,
               };
               doConfirm(formData);
            });
            $userInfo.show();

         }
      });
   }

   function doConfirm(sendMoneyData) {
      $.ajax({
         url: "/api/money/confirm",
         type: "POST",
         headers: createAuthorizationTokenHeader(),
         data: JSON.stringify(sendMoneyData),
         contentType: "application/json; charset=utf-8",
         dataType: "json",
         success: function (data, textStatus, jqXHR) {
            setJwtToken(data.id_token);
            location.reload(true);
         }
      });
   }


   function showResponse(statusCode, message) {
      $response
         .empty()
         .text(
            "status code: "
            + statusCode + "\n-------------------------\n"
            + (typeof message === "object" ? JSON.stringify(message) : message)
         );
   }

   // REGISTER EVENT LISTENERS =============================================================
   $("#loginForm").submit(function (event) {
      event.preventDefault();

      var $form = $(this);
      var formData = {
         username: $form.find('input[name="username"]').val(),
         password: $form.find('input[name="password"]').val()
      };

      doLogin(formData);
   });

   $("#logoutButton").click(doLogout);

   $("#exampleServiceBtn").click(function () {
      $.ajax({
         url: "/api/person",
         type: "GET",
         contentType: "application/json; charset=utf-8",
         dataType: "json",
         headers: createAuthorizationTokenHeader(),
         success: function (data, textStatus, jqXHR) {
            showResponse(jqXHR.status, JSON.stringify(data));
         },
         error: function (jqXHR, textStatus, errorThrown) {
            showResponse(jqXHR.status, jqXHR.responseJSON.message)
         }
      });
   });

   $("#adminServiceBtn").click(function () {
      $.ajax({
         url: "/api/hiddenmessage",
         type: "GET",
         contentType: "application/json; charset=utf-8",
         dataType: "json",
         headers: createAuthorizationTokenHeader(),
         success: function (data, textStatus, jqXHR) {
            showResponse(jqXHR.status, data);
         },
         error: function (jqXHR, textStatus, errorThrown) {
            showResponse(jqXHR.status, jqXHR.responseJSON.message)
         }
      });
   });

   $loggedIn.click(function () {
      $loggedIn
         .toggleClass("text-hidden")
         .toggleClass("text-shown");
   });

   // INITIAL CALLS =============================================================
   if (getJwtToken()) {
      $login.hide();
      $notLoggedIn.hide();
      //      showTokenInformation();
      showUserInformation();
   }
});
