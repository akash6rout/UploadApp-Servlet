package com.nt.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;

@WebServlet("/download")
public class JobSeekersDownloadServlet extends HttpServlet {
	
	private static final String GET_RESUME_PATH="SELECT JSRESUME_PATH FROM JOBSEEKER_INFO WHERE JSID=?";
	private static final String GET_PHOTO_PATH="SELECT JSPHOTO_PATH FROM JOBSEEKER_INFO WHERE JSID=?";

	
	
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		//read additional request param value
		String query=null;
		String pval=req.getParameter("pid");
		query=GET_PHOTO_PATH;
		if(pval==null) {
			pval=req.getParameter("rid");
			query=GET_RESUME_PATH;
		}
		//Load jdbc driver class
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		try(Connection con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","root");
				PreparedStatement ps=con.prepareStatement(query);
				){
			if(ps!=null) {
				//set value query params
				ps.setInt(1, Integer.parseInt(pval));
			}
			//execute the query
			String filePath=null;
			try(ResultSet rs=ps.executeQuery()){
				//process the ResultSet
				if(rs!=null) {
					if(rs.next()) {
						filePath=rs.getString(1);
					}
				}
			}
			//file download operation
			//create file object havaing file name and loacation
			File file=new File(filePath);
			//get the length of the file and make it as response content length
			res.setContentLengthLong(file.length());
			//get MIME type of the file and make it response content type
			ServletContext sc=getServletContext();
			String mimeType=sc.getMimeType(filePath);
			mimeType=(mimeType==null)?"application/octet-stream":mimeType;
			res.setContentType(mimeType);
			//Give instruction to browser to make the response content as downloadadble file
			InputStream is=new FileInputStream(file);
			//create OutputStream pointing to response object
			OutputStream os=res.getOutputStream();
			//copy the file content
			IOUtils.copy(is,os);//add commons-IO-<ver>.jar file classpath download from internet
			//close the streams
			is.close();
			os.close();
		}
		catch(SQLException se) {
			se.printStackTrace();
		}
	}

	
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		doGet(req, res);
	}

}
