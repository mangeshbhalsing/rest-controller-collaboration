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

import com.niit.collaboration.dao.BlogCommentDAO;

import com.niit.collaboration.model.BlogComment;

@CrossOrigin("http://localhost:8077")
@RestController
public class BlogCommentRestService {
	
	
	private Logger log = LoggerFactory.getLogger(BlogCommentRestService.class);
	
	@Autowired
	private BlogComment blogComment;
	
	@Autowired
	private BlogCommentDAO	blogCommentDAO;
	
	@Autowired
	private HttpSession session;
	
	@GetMapping("/helloco")
	public String sayHello()
	{
		return "  Hello from blogcomment  Modifed message";
	}
	
	@RequestMapping(value = "/createComment", method=RequestMethod.POST)
	public ResponseEntity<BlogComment> CreateComment(@RequestBody BlogComment blogcomment){
		
		if (blogCommentDAO.saveComment(blogcomment) ==true)
		  {
			  blogComment.setErrorCode("200");
			blogComment.setErrorMessage("Comment created Succesfully" );
		  }
		  else
		  {
			  blogComment.setErrorCode("404");
				blogComment.setErrorMessage("Failed to create a Comment");  
		  }
		return new ResponseEntity<BlogComment>(blogComment,HttpStatus.OK);		
	}
	
	
	
	
	@RequestMapping(value ="/blog_Comments/{id}", method = RequestMethod.GET)
	public ResponseEntity<List<BlogComment>> getCommentOfBlogId(@PathVariable("id") int id){
		List<BlogComment> blogCommentlist =  blogCommentDAO.getComments(id);
		/*if(blogComment == null){
		    blog1 = new Blog();
		    blog1.setId(0);
			blog1.setErrorCode("404");
			blog1.setErrorMessage("No Events posted!");
		}*/
		return new ResponseEntity<List<BlogComment>> (blogCommentlist, HttpStatus.OK);
	}
	

	@RequestMapping(value = "/allCommets", method = RequestMethod.GET)
	public ResponseEntity<List<BlogComment>> getBlogCommentList() {
		List<BlogComment> blogList = blogCommentDAO.list();
		if (blogList.isEmpty()) {
			BlogComment blogComment = new BlogComment();
			blogComment.setErrorCode("404");
			blogComment.setErrorMessage("Blogs are not available");
			//blogList.add(blog);
		}
		return new ResponseEntity<List<BlogComment>>(blogList, HttpStatus.OK);
	}
	
	
	@RequestMapping(value= "/deleteComment/{id}",  method = RequestMethod.GET)
	public ResponseEntity<BlogComment> deleteBlogCommentbyId(@PathVariable("id") int id){
		
		
		if(blogCommentDAO.deleteComment(id) == false){
			BlogComment blogComment = new BlogComment();
			blogComment.setErrorCode("404");
			blogComment.setErrorMessage("No Events posted!");
		}
		blogCommentDAO.deleteComment(id);
		return new ResponseEntity<BlogComment>(blogComment,HttpStatus.OK);
	}
	

}
