package com.websystique.springmvc.controller;
 
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Base64;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import com.mysql.jdbc.util.Base64Decoder;
import com.websystique.springmvc.model.Album;
import com.websystique.springmvc.model.User;
import com.websystique.springmvc.service.AlbumService;
import com.websystique.springmvc.service.UserService;
import com.websystique.springmvc.util.FileValidator;
 
@Controller
@RestController
public class HelloWorldRestController {
 
    @Autowired
    UserService userService;  //Service which will do all data retrieval/manipulation work
  
	
	@Autowired
	AlbumService albumService;
	
	@Autowired
	MessageSource messageSource;

	@Autowired
	FileValidator fileValidator;
	
	@InitBinder("fileBucket")
	protected void initBinder(WebDataBinder binder) {
	   binder.setValidator(fileValidator);
	}
	
 
    
 
    
    //-------------------Retrieve Single User--------------------------------------------------------
     
	@RequestMapping(value = { "/profile/" }, method = RequestMethod.POST)
    public ResponseEntity<User> getUser(@RequestBody int id) throws UnsupportedEncodingException {
		User user=userService.findById(id);
		if (user == null) {
            System.out.println("User not found");
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }    
		
	
		return new ResponseEntity<User>(user, HttpStatus.OK);
    }
 
	
     
    @RequestMapping(value="/login/",method=RequestMethod.POST)
    public ResponseEntity<User>  login(@RequestBody User user){
   
    	User u=userService.findByEmail(user.getEmail());
    	if(!(u==null)){
    		
    		return new ResponseEntity<User>(u, HttpStatus.OK);
    	}else{
    		
    		return new ResponseEntity<User>(u, HttpStatus.NOT_FOUND); 
    	}
		
    }
    //-------------------Create a User--------------------------------------------------------
     
    @RequestMapping(value = "/user/", method = RequestMethod.POST,consumes = { "application/json" })
    public ResponseEntity<User> createUser(@RequestBody User user) {
    	
    	System.out.println("Creating User " + user.toString());
        if (!(userService.isUserSSOUnique(user.getEmail()))) {
            System.out.println("A User with name " + user.getFirstName() + " already exist");
            return new ResponseEntity<User>(HttpStatus.CONFLICT);
        }
        userService.saveUser(user);
        
        HttpHeaders headers = new HttpHeaders();
        return new ResponseEntity<User>(user,HttpStatus.CREATED);
    }
 
    
     
    //------------------- Search Friend --------------------------------------------------------
 
    @RequestMapping(value = "/searchFriend", method = RequestMethod.POST)
    public ResponseEntity<List<User>> searchFriend(@RequestBody String friend) {
    
      StringTokenizer st=new StringTokenizer(friend);
      List<User> users=new ArrayList<User>();
     
	      while(st.hasMoreTokens()){
	    	
	    	users.addAll(userService.findByLastname(st.nextToken()));
	    	
	      }
     
	      users=ordinaryList(users);
	      if (users.isEmpty()){
	    	  return new ResponseEntity<List<User>>(HttpStatus.NOT_FOUND);
	      }else{
	          return new ResponseEntity<List<User>>(users,HttpStatus.OK);
	
	      }
      }
	
    

 
   
    	@RequestMapping(value = "/searchImage", method = RequestMethod.POST)
        public ResponseEntity<List<Album>> searchImage(@RequestBody String image) {
        	List<Album> albums=new ArrayList<Album>();
        	StringTokenizer st=new StringTokenizer(image);
        	
        	while(st.hasMoreTokens()){
        	
        		albums.addAll(albumService.findByMetaTag(st.nextToken()));
        	}
        	
        	if(albums.isEmpty()){
  	    	  return new ResponseEntity<List<Album>>(HttpStatus.NOT_FOUND);

        	}else{
        		System.out.println(albums);
  	          return new ResponseEntity<List<Album>>(albums,HttpStatus.OK);

        	}
	}





	//------------------- Delete a User --------------------------------------------------------
     
    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<User> deleteUser(@PathVariable("id") int id) {
        System.out.println("Fetching & Deleting User with id " + id);
 
        User user = userService.findById(id);
        if (user == null) {
            System.out.println("Unable to delete. User with id " + id + " not found");
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        }
 
        return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
    }
 
     
    
    //------------------- Upload Profile Photo User --------------------------------------------------------
     
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<User> upload(@RequestBody User user) throws IOException {
    	
    	User u=userService.findById(user.getId());
    	if(u==null){
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
    	}else{
    		
	    	userService.updateUser(user.getId(), user.getPhoto());
	    	return new ResponseEntity<User>(user,HttpStatus.OK);
    	}
    }
   
    
    public List<User> ordinaryList(List<User> user){
        Collections.sort(user, new Comparator<User>(){
           public int compare(User u1, User u2){
              return (u1.getLastName().compareTo(u2.getLastName()));
           }
        });
        return user;   
     }
    
   
}