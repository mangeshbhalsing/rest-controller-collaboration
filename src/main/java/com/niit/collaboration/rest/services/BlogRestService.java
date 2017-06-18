
package com.niit.collaboration.rest.services;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.niit.collaboration.dao.BlogDAO;
import com.niit.collaboration.model.Blog;
import com.niit.collaboration.model.Blog;

@CrossOrigin(origins="http://localhost:8077")
@RestController
public class BlogRestService {
	
	private Logger log = LoggerFactory.getLogger(BlogRestService.class);
	
	@Autowired
	private Blog blog;
	
	@Autowired
	private BlogDAO blogDAO;
	
	@Autowired
	private HttpSession session;
	
	@GetMapping("/helloSir")
	public String sayHello()
	{
		return "  Hello from Blog rest service Modifed message";
	}
	
	/*To Create a Blog In Rest services*/
	
	@RequestMapping(value = "/CreateBlog", method = RequestMethod.POST)
	public boolean Createblog(@RequestBody Blog blog) {
		 blog.setStatus("N");  ///bydefault we have to set the initial status will be 'N"

		if (blogDAO.save(blog)) {
			
			blog.setErrorCode("200");
			blog.setErrorMessage("Seccessfully posted!");
			return true;
		} else {
			
			blog.setErrorCode("404");
			blog.setErrorMessage("Not registered!");
			return false;

		}

	}
	
	/*To fetch the Blog with Help of Id*/
	
	@RequestMapping(value ="/blog/{id}", method = RequestMethod.GET)
	public ResponseEntity<Blog> getBlog(@PathVariable("id") String id){
		Blog blog1 =  blogDAO.getBlogById(id);
		if(blog1 == null){
		    blog1 = new Blog();
		    blog1.setId(0);
			blog1.setErrorCode("404");
			blog1.setErrorMessage("No Events posted!");
		}
		return new ResponseEntity<Blog>(blog1, HttpStatus.OK);
	}
	
	/*To fetch the All The Blog*/
	
	@RequestMapping(value = "/blogs", method = RequestMethod.GET)
	public ResponseEntity<List<Blog>> getBlogList() {
		List<Blog> blogList = blogDAO.list();
		if (blogList.isEmpty()) {
			Blog blog = new Blog();
			blog.setErrorCode("404");
			blog.setErrorMessage("Blogs are not available");
			blogList.add(blog);
		}
		return new ResponseEntity<List<Blog>>(blogList, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/blogsPending", method = RequestMethod.GET)
	public ResponseEntity<List<Blog>> getBlogListPending() {
		List<Blog> blogList = blogDAO.listPending();
		if (blogList.isEmpty()) {
			Blog blog = new Blog();
			blog.setErrorCode("404");
			blog.setErrorMessage("Blogs are not available");
			blogList.add(blog);
		}
		return new ResponseEntity<List<Blog>>(blogList, HttpStatus.OK);
	}
	
	
	/*To Delete the Blog With Id*/
	
	@RequestMapping(value= "/deleteBlog/{id}",  method = RequestMethod.GET)
	public boolean deleteBlog(@PathVariable("id") String id){
		
		
		if(blogDAO.getBlogById(id) == null){
			Blog blog = new Blog();
			blog.setErrorCode("404");
			blog.setErrorMessage("No Events posted!");
		}
		blogDAO.deleteById(id);
		return true ;
	}
	
	
	//Tested
	
	/*To Update the Blog with Id*/
	
	@RequestMapping(value = "/updateBlog/{id}", method = RequestMethod.POST)
	public ResponseEntity<Blog> updateBlog(@PathVariable("id") String id, @RequestBody Blog blog){
		if(blogDAO.getBlogById(id) == null){
			blog.setErrorCode("404");
			blog.setErrorMessage("No Events posted!");
		}else{
			blogDAO.update(blog);
			blog.setErrorCode("200");
			blog.setErrorMessage("Saved the Blog Successfully!");
			}
		return new ResponseEntity<Blog>(blog, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/rejectBlog/{id}/{reason}", method = RequestMethod.GET)
	public ResponseEntity<Blog> reject(@PathVariable("id") String id, @PathVariable("reason") String reason) {
		log.debug("Starting of the method reject");

		blog = updateStatus(id, "R", reason);
		log.debug("Ending of the method reject");
		return new ResponseEntity<Blog>(blog, HttpStatus.OK);

	}

	private Blog updateStatus(String id, String status, String reason) {
		log.debug("Starting of the method updateStatus");

		log.debug("status: " + status);
		blog = blogDAO.getBlogById(id);

		if (blog == null) {
			blog = new Blog();
			blog.setErrorCode("404");
			blog.setErrorMessage("Could not update the status to " + status);
		} else {

			blog.setStatus(status);
			blog.setReason(reason);
			
			blogDAO.update(blog);
			
			blog.setErrorCode("200");
			blog.setErrorMessage("Updated the status successfully");
		}
		log.debug("Ending of the method updateStatus");
		return blog;

	}
	
	@RequestMapping(value = "/acceptBlog/{id}", method = RequestMethod.GET)
	public ResponseEntity<Blog> accept(@PathVariable("id") String id) {
		log.debug("Starting of the method accept");

		blog = updateStatus(id, "A", "");
		log.debug("Ending of the method accept");
		return new ResponseEntity<Blog>(blog, HttpStatus.OK);

	}
	
	

}
