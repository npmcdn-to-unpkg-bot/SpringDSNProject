'user strict';
App.controller('UserController',['UserService','$window','$scope',function (UserService,$window,$scope){
		var self=this;
         self.user={id:null,birth_day:'',city:'',email:'',firstName:'',lastName:'',photo:null,pw:''};
         var message='';    
        
         
         
         
         function readID(){
             var url = window.location.pathname;
             var id_utente = url.substring(url.lastIndexOf('/') + 1);
             return id_utente;
             }
         
         
       self.createUser=function(){
    	   	var utente=$scope.ctrl.user;
    	    var key = CryptoJS.enc.Base64.parse("MTIzNDU2NzgxMjM0NTY3OA==");
    	    var iv  = CryptoJS.enc.Base64.parse("EBESExQVFhcYGRobHB0eHw==");
    	    var encrypted = CryptoJS.AES.encrypt(utente.pw, key, {iv: iv});
    	    utente.pw=encrypted.toString();
            UserService.saveUser(utente)
		              .then(
		            		  function(d){
		            			  self.id=d.id;
		            			  
		            			$window.location.href='http://localhost:8080/Spring4MVCAngularJSExample/uploadPhoto/'+self.id;
		            			
		            		  },
				              function(errResponse){
					               console.error('Error while creating User.');

				              }	
                 );
         };

         self.uploadPhoto=function(){
        	 var file=$scope.myFile;
        	 var id=readID();
        	console.log(id);
        	UserService.uploadPhoto(id,file)
        		.then(
        				function(){
	            			$window.location.href='http://localhost:8080/Spring4MVCAngularJSExample/';
;
        				},
        				 function(errResponse){
				               console.error('Error while creating User.');

			              }	);
         };
         
         self.reset=function(){
        	 $scope.user.email='';
        	 $scope.user.pw='';
        	 $scope.user.birth_day='';
        	 $scope.user.city='';
        	 $scope.user.firstName='';
        	 $scope.user.lastName='';
        	 $scope.user.photo=';'
             $scope.myForm.$setPristine(); //reset Form
         };

         self.submit=function(){
			$scope.createUser();
			$scope.reset();
		};
	}]);

App.directive('fileModel', ['$parse', function ($parse) {
	
	    return {
	        restrict: 'A',
	        link: function(scope, element, attrs) {
	            var model = $parse(attrs.fileModel);
	            var modelSetter = model.assign;
	            
	            element.bind('change', function(){
	                scope.$apply(function(){
	                    modelSetter(scope, element[0].files[0]);
	                });
	            });
	        }
	    };

}]);
