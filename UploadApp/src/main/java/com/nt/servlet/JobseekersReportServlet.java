package com.nt.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/jsReport")
public class JobseekersReportServlet extends HttpServlet {
	private static final String JOBSEEKER_SELECT_QUERY="SELECT * FROM JOBSEEKER_INFO";
   
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		//Get PrintWriter object
		PrintWriter pw=res.getWriter();
		//set response content type
		res.setContentType("text/html");
		//Load jdbc class
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		try(Connection con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","system","root");
				PreparedStatement ps=con.prepareStatement(JOBSEEKER_SELECT_QUERY);
				ResultSet rs=ps.executeQuery()){
			
			if(rs!=null) {
				pw.println("<h1 style='color:red;text-align:center'>JobSeeker Report</h1>");
				pw.println("<table border='1' align='center' bgcolor='pink'>");
				pw.println("<tr><th>JsId</th><th>JsName</th><th>JsAddrs</th><th>Photo</th><th>Resume</th></tr>");
				while(rs.next()) {
					pw.println("<tr>");
					pw.println("<td>"+rs.getInt(1)+"</td><td>"+rs.getString(2)+"</td><td>"+rs.getString(3)+"</td><td><a href='download?pid="+rs.getInt(1)+"'>download</a></td><td><a href='download?rid="+rs.getInt(1)+"'>download</a></td>"); 
					pw.println("</tr>");
				}
				pw.println("</table>");
			}
			
		}
		catch(SQLException se) {
			se.printStackTrace();
		}
		catch(Exception e) {
			e.printStackTrace();
		}

	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		doGet(req, res);
	}

}
