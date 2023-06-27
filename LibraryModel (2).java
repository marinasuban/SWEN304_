/*
 * LibraryModel.java
 * Author: Kamonchanok Suban Na Ayudtaya
 * Created on: 01/06/23
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import javax.swing.*;

public class LibraryModel {

    // For use in creating dialogs and making them modal
    public JFrame dialogParent;
    public Connection con; // added connection

    public LibraryModel(JFrame parent, String userid, String password) {
        dialogParent = parent;

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException cnfe) {
            System.out.println(
                    "Can not find the driver class: " +
                            "\nEither I have not installed it" +
                            "properly or \n postgresql.jar " +
                            " file is not in my CLASSPATH");
            System.exit(1);
        }

        String url = "jdbc:postgresql://localhost:5433/subankamo_jdbc";
        try {
            con = DriverManager.getConnection(url, userid, password);
            System.out.println("Connected to the database");
        } catch (SQLException sqlex) {
            System.out.println("Can not connect");
            System.out.println(sqlex.getMessage());
            System.exit(1);
        }
    }

    /*
     * Show book information that matches an isbn sorted according to AuthSeqNo
     * 
     * @param isbn
     * 
     * @return
     * ------RESULT OF BOOK LOOKUP------
     * ISBN: 1001
     * TITLE: Database Modeling & Design
     * EDITION: 1
     * NUMBER OF COPIES: 1
     * COPIES REMAINING: 1
     * AUTHOR:
     * 1. Marina Suban
     */
    public String bookLookup(int isbn) {
        // Return String Value
        String title = "NO RESULTS FOUND - Check you have the correct isbn";
        String editionNumber = "";
        String numberOfCopies = "";
        String numberLeft = "";
        String author = "";

        try {
            // Create query to get matching isbn results
            String selectBookByISBN = "SELECT b.ISBN, b.Title, b.Edition_No, b.NumOfCop, b.NumLeft, ba.AuthorSeqNo, a.Name, a.Surname "
                    + "FROM Book AS b "
                    + "LEFT JOIN Book_Author AS ba ON b.ISBN = ba.ISBN "
                    + "LEFT JOIN Author AS a ON a.AuthorID = ba.AuthorID "
                    + "WHERE b.ISBN = " + isbn + " "
                    + "ORDER BY ba.AuthorSeqNo;";
            // Create Statement
            Statement stmt = con.createStatement();
            // Execute Query
            ResultSet result = stmt.executeQuery(selectBookByISBN);
            // While there are unprocessed results set value to return string variables
            while (result.next()) {
                title = result.getString("Title");
                editionNumber = result.getString("Edition_No");
                numberOfCopies = result.getString("NumOfCop");
                numberLeft = result.getString("NumLeft");
                author += "\n" + result.getString("AuthorSeqNo") + ". " + result.getString("Name")
                        + result.getString("Surname");
            }
            // When all finished results are processed close statement
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return "AN UNEXPECTED ERROR HAS OCCURED";
        }

        // Return query result
        return "------RESULT OF BOOK LOOKUP------"
                + "\nISBN: " + isbn
                + "\nTITLE: " + title
                + "\nEDITION: " + editionNumber
                + "\nNUMBER OF COPIES: " + numberOfCopies
                + "\nCOPIES REMAINING: " + numberLeft
                + "\nAUTHOR/S: " + author;
    }

    /***
     * Show all books in the library and their details.
     * 
     * @return
     *         ------ALL BOOKS IN CATALOGUE------
     *         ISBN: 1001
     *         TITLE: Database Modeling & Design
     *         EDITION: 1
     *         NUMBER OF COPIES: 1
     *         COPIES REMAINING: 1
     */
    public String showCatalogue() {
        // Return String Value
        String processedResult = "------ALL BOOKS IN CATALOGUE------\n";

        try {
            // Create query to get record from book
            String selectAllBook = "SELECT * FROM book ORDER BY isbn ASC;";
            // Create Statement
            Statement stmt = con.createStatement();
            // Execute Query
            ResultSet result = stmt.executeQuery(selectAllBook);
            // While there are unprocessed results set value to return string variables
            while (result.next()) {
                String ISBN = result.getString("ISBN");
                String title = result.getString("Title");
                String editionNumber = result.getString("Edition_No");
                String numberOfCopies = result.getString("NumOfCop");
                String numberLeft = result.getString("NumLeft");
                processedResult += ("\nISBN: " + ISBN
                        + "\nTITLE: " + title
                        + "\nEDITION: " + editionNumber
                        + "\nNUMBER OF COPIES: " + numberOfCopies
                        + "\nCOPIES REMAINING: " + numberLeft + "\n");
            }
            // When all finished results are processed close statement
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return "AN UNEXPECTED ERROR HAS OCCURED";
        }

        // Return query result
        return processedResult;
    }

    /***
     * Show all books that have been loaned by a customer and relevant details
     * 
     * @return
     *         ------ALL BOOKS ON LOAN------
     *         CUSTOMER ID: 1
     *         DUE DATE: null
     *         ISBN: 1001
     *         TITLE: Database Modeling & Design
     *         EDITION: 1
     *         NUMBER OF COPIES: 1
     *         COPIES REMAINING: 1
     */
    public String showLoanedBooks() {
        // Return String Value
        String processedResult = "------ALL BOOKS ON LOAN------\n";

        try {
            // Create query to get record from book
            String selectAllBookOnLoan = "SELECT * FROM cust_book NATURAL JOIN book;";
            // Create Statement
            Statement stmt = con.createStatement();
            // Execute Query
            ResultSet result = stmt.executeQuery(selectAllBookOnLoan);
            // While there are unprocessed results set value to return string variables
            while (result.next()) {
                String customerId = result.getString("CustomerId");
                String dueDate = result.getString("DueDate");
                String ISBN = result.getString("ISBN");
                String title = result.getString("Title");
                String editionNumber = result.getString("Edition_No");
                String numberOfCopies = result.getString("NumOfCop");
                String numberLeft = result.getString("NumLeft");
                processedResult += ("\nCUSTOMER ID: " + customerId
                        + "\nDUE DATE: " + dueDate
                        + "\nISBN: " + ISBN
                        + "\nTITLE: " + title
                        + "\nEDITION: " + editionNumber
                        + "\nNUMBER OF COPIES: " + numberOfCopies
                        + "\nCOPIES REMAINING: " + numberLeft + "\n");
            }
            // When all finished results are processed close statement
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return "AN UNEXPECTED ERROR HAS OCCURED";
        }

        // Return query result
        return processedResult;
    }

    /*
     * Show author information that matches an authorID
     * 
     * @param authorID
     * 
     * @return
     * ------RESULT OF AUTHOR LOOKUP------
     * AUTHOR ID: 1001
     * AUTHOR'S NAME:
     * Marina Suban
     */
    public String showAuthor(int authorID) {
        // Return String Value
        String authorName = "NO RESULTS FOUND - Check you have the correct authorID";

        try {
            // Create query to get matching authorID results
            String selectAuthorbyAuthorId = "SELECT name,surname FROM author WHERE authorID =" + authorID + ";";
            // Create Statement
            Statement stmt = con.createStatement();
            // Execute Query
            ResultSet result = stmt.executeQuery(selectAuthorbyAuthorId);
            // While there are unprocessed results set value to return string variables
            while (result.next()) {
                authorName = result.getString("Name") + result.getString("Surname");
            }
            // When all finished results are processed close statement
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return "AN UNEXPECTED ERROR HAS OCCURED";
        }

        // Return query result
        return "------RESULT OF AUTHOR LOOKUP------"
                + "\nAUTHOR ID: " + authorID
                + "\nAUTHOR'S NAME: " + authorName;
    }

    /*
     * Show author information that matches an authorID
     * 
     * @return
     * ------ALL AUTHORS------
     * AUTHOR ID: 13
     * AUTHOR'S NAME: Raghu Ramakrishnan
     */
    public String showAllAuthors() {
        // Return String Value
        String processedResult = "------ALL AUTHORS------\n";

        try {
            // Create query to get matching authorID results
            String selectAuthors = "SELECT * FROM author ORDER BY authorId ASC;";
            // Create Statement
            Statement stmt = con.createStatement();
            // Execute Query
            ResultSet result = stmt.executeQuery(selectAuthors);
            // While there are unprocessed results set value to return string variables
            while (result.next()) {
                processedResult += "\nAUTHOR ID: " + result.getString("AuthorId")
                        + "\nAUTHOR'S NAME: " + result.getString("Name") + result.getString("Surname") + "\n";
            }
            // When all finished results are processed close statement
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return "AN UNEXPECTED ERROR HAS OCCURED";
        }

        // Return query result
        return processedResult;
    }

    /*
     * Show customer information that matches a customerID
     * 
     * @param customerID
     * 
     * @return
     * ------RESULT OF CUSTOMER LOOKUP------
     * CUSTOMER ID: 2
     * CUSTOMER'S NAME: Marina Suban
     * CITY: Blenheim
     */
    public String showCustomer(int customerID) {
        // Return String Value
        String customerName = "NO RESULTS FOUND - Check you have the correct authorID";
        String city = "";

        try {
            // Create query to get matching authorID results
            String selectCustomerByCustomerId = "SELECT f_name,l_name,City FROM customer WHERE customerid = "
                    + customerID
                    + ";";
            // Create Statement
            Statement stmt = con.createStatement();
            // Execute Query
            ResultSet result = stmt.executeQuery(selectCustomerByCustomerId);
            // While there are unprocessed results set value to return string variables
            while (result.next()) {
                customerName = result.getString("f_name") + result.getString("l_name");
                city = result.getString("city");
            }
            // When all finished results are processed close statement
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return "AN UNEXPECTED ERROR HAS OCCURED";
        }

        // Return query result
        return "------RESULT OF CUSTOMER LOOKUP------"
                + "\nCUSTOMER ID: " + customerID
                + "\nCUSTOMER'S NAME: " + customerName
                + "\nCITY: " + city;

    }

    /*
     * Show all customer information
     * 
     * @return
     * ------ALL CUSTOMERS------
     * CUSTOMER ID: 1
     * CUSTOMER'S NAME: Kirk Jackson
     * CITY: Wellington
     */
    public String showAllCustomers() {
        // Return String Value
        String processedResult = "------ALL CUSTOMERS------\n";

        try {
            // Create query to get matching authorID results
            String selectCustomers = "SELECT * FROM customer ORDER BY customerId ASC;";
            // Create Statement
            Statement stmt = con.createStatement();
            // Execute Query
            ResultSet result = stmt.executeQuery(selectCustomers);
            // While there are unprocessed results set value to return string variables
            while (result.next()) {
                processedResult += "\nCUSTOMER ID: " + result.getString("CustomerId")
                        + "\nCUSTOMER'S NAME: " + result.getString("f_name") + result.getString("l_name")
                        + "\nCITY: " + result.getString("city") + "\n";
            }
            // When all finished results are processed close statement
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return "AN UNEXPECTED ERROR HAS OCCURED";
        }

        // Return query result
        return processedResult;
    }

    /*
     * Borrow a book from the library
     * 
     * @return
     * SUCCESS: Customer 1234 borrowed book 5678 on 2023-05-01
     */
    public String borrowBook(int isbn, int customerID, int day, int month, int year) {
        Statement stmt = null;
        try {
            // Create Statement
            con.setAutoCommit(false);
            stmt = con.createStatement();
            // check if customer exist
            String checkCustomerExist = "SELECT * FROM Customer WHERE customerid=" + customerID;
            ResultSet rsCustomerExist = stmt.executeQuery(checkCustomerExist);
            if (rsCustomerExist.next() == false) {
                stmt.execute("rollback;");
                con.setAutoCommit(true);
                return "CANNOT BORROW: customer not found with customerId " + customerID
                        + " - check customerID and try again!";
            } else {
                stmt.execute("BEGIN");
                stmt.execute("LOCK Customer IN ROW SHARE MODE;");
                // Check Book exist
                String checkBookExist = "SELECT * FROM book WHERE isbn = " + isbn;
                ResultSet rsBookExist = stmt.executeQuery(checkBookExist);
                if (rsBookExist.next() == false) {
                    stmt.execute("rollback;");
                    con.setAutoCommit(true);
                    return "CANNOT BORROW: book not found with ISBN " + isbn
                            + " - check ISBN and try again!";
                } else {
                    int numberOfBooksLeft = rsBookExist.getInt("numLeft");
                    // Check if any books avaliable for loan
                    if (numberOfBooksLeft <= 0) {
                        stmt.execute("rollback;");
                        con.setAutoCommit(true);
                        return "CANNOT BORROW: no avaliable copies - check back later and try again!";
                    } else {
                        stmt.execute("LOCK book IN ROW SHARE MODE;");

                        // Set frame
                        JFrame frame = new JFrame();
                        frame.setSize(300, 300);
                        frame.setLayout(null);
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                        // Confirm Action
                        int a = JOptionPane.showConfirmDialog(frame, "Are you sure that you want to borrow this book?");
                        if (a == JOptionPane.YES_OPTION) {
                            LocalDate date = LocalDate.of(year, month, day);
                            stmt.executeUpdate(
                                    "INSERT INTO cust_book VALUES('" + isbn + "','" + date + "','" + customerID
                                            + "');");
                            stmt.executeUpdate("UPDATE book SET numleft = numleft-1 WHERE isbn =" + isbn + " ;");
                            stmt.execute("commit;");
                            con.setAutoCommit(true);
                            return "SUCCESS: Customer " + customerID
                                    + " borrowed book " + isbn
                                    + " on " + date;
                        } else {
                            stmt.execute("rollback;");
                            con.setAutoCommit(true);
                            return "BORROWING CANCELLED: Customer " + customerID
                                    + " did not confirm borrowing the book.";
                        }
                    }
                }
            }
        } catch (SQLException e) {
            try {
                stmt.execute("rollback;");
                con.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return "AN UNEXPECTED ERROR HAS OCCURED";
        }
    }

    /*
     * Return a book borrowed from the library
     * 
     * @return
     * SUCCESS: Customer 1234 returned book 5678 on 2023-05-01
     */
    public String returnBook(int isbn, int customerid) {
        Statement stmt = null;
        try {
            // Create Statement
            con.setAutoCommit(false);
            stmt = con.createStatement();
            // check if customer exist
            String checkCustomerExist = "SELECT * FROM Customer WHERE customerid=" + customerid;
            ResultSet rsCustomerExist = stmt.executeQuery(checkCustomerExist);
            if (rsCustomerExist.next() == false) {
                stmt.execute("rollback;");
                con.setAutoCommit(true);
                return "CANNOT RETURN: customer not found with customerId " + customerid
                        + " - check customerID and try again!";
            } else {
                stmt.execute("BEGIN");
                stmt.execute("LOCK Customer IN ROW SHARE MODE;");
                // Check Book exist
                String checkBookExist = "SELECT * FROM book WHERE isbn = " + isbn;
                ResultSet rsBookExist = stmt.executeQuery(checkBookExist);
                if (rsBookExist.next() == false) {
                    stmt.execute("rollback;");
                    con.setAutoCommit(true);
                    return "CANNOT RETURN: book not found with ISBN " + isbn
                            + " - check ISBN and try again!";
                } else {
                    String checkCustomerBorrowedBook = "SELECT * FROM cust_book where isbn =" + isbn
                            + " AND customerid = " + customerid;
                    ResultSet rsCustomerBorrowedBook = stmt.executeQuery(checkCustomerBorrowedBook);
                    if (rsCustomerBorrowedBook.next() == false) {
                        stmt.execute("rollback;");
                        con.setAutoCommit(true);
                        return "CANNOT RETURN: book " + isbn + " was not borrowed by customer " + customerid;
                    } else {
                        stmt.execute("LOCK book IN ROW SHARE MODE;");

                        // Set frame
                        JFrame frame = new JFrame();
                        frame.setSize(300, 300);
                        frame.setLayout(null);
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                        // Confirm Action
                        int a = JOptionPane.showConfirmDialog(frame, "Are you sure that you want to return this book?");
                        if (a == JOptionPane.YES_OPTION) {
                            stmt.executeUpdate("DELETE FROM cust_book WHERE customerid =" + customerid + ";");
                            stmt.executeUpdate("UPDATE book SET numleft = numleft+1 WHERE isbn =" + isbn + " ;");
                            stmt.execute("commit;");
                            con.setAutoCommit(true);
                            return "SUCCESS: Customer " + customerid
                                    + " returned book " + isbn;
                        } else {
                            stmt.execute("rollback;");
                            con.setAutoCommit(true);
                            return "RETURN CANCELLED: Customer " + customerid
                                    + " did not confirm returning the book.";
                        }
                    }
                }
            }
        } catch (SQLException e) {
            try {
                stmt.execute("rollback;");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return "AN UNEXPECTED ERROR HAS OCCURED";
        }
    }

    public void closeDBConnection() {
        try {
            con.close();
            System.out.println("Connection with Database has been terminated.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("AN UNEXPECTED ERROR HAS OCCURED");

        }
    }

    /*
     * Delete customer from records
     * 
     * @return
     * SUCCESS: Customer with customerID of 1234 has been successfully deleted
     */
    public String deleteCus(int customerID) {
        try {
            // Create Statement
            Statement stmt = con.createStatement();
            // Check if author exist
            String checkCustomerExist = "SELECT * FROM Customer WHERE customerid=" + customerID;
            ResultSet rsCustomerExist = stmt.executeQuery(checkCustomerExist);
            // if not do nothing
            if (rsCustomerExist.next() == false) {
                return "CANNOT DELETE: Could not find customer with customer ID of " + customerID
                        + " - Please check the customer ID and try again!";
            } else {
                String checkCustomerLoanedBook = "SELECT * FROM cust_book WHERE customerid=" + customerID;
                ResultSet rsCustomerLoanedBook = stmt.executeQuery(checkCustomerLoanedBook);
                if (rsCustomerLoanedBook.next()) {
                    return "CANNOT DELETE: Customer with customer id" + customerID
                            + " cannot be deleted - customer has books on loan";
                }
                // otherwise delete customer record from relevant tables
                else {
                    stmt.executeUpdate("DELETE FROM customer WHERE customerid = " + customerID);
                    return "SUCCESS: Customer with customer ID of " + customerID + " has been successfully deleted";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "AN UNEXPECTED ERROR HAS OCCURED";
        }
    }

    /*
     * Delete author from records
     * 
     * @return
     * SUCCESS: Author with authorID of 1234 has been successfully deleted
     */
    public String deleteAuthor(int authorID) {
        try {
            // Create Statement
            Statement stmt = con.createStatement();
            // Check if author exist
            String checkAuthorExist = "SELECT * FROM Author WHERE authorid=" + authorID;
            ResultSet rsAuthorExist = stmt.executeQuery(checkAuthorExist);
            // if not do nothing
            if (rsAuthorExist.next() == false) {
                return "CANNOT DELETE: Could not find author with author ID of " + authorID
                        + " - Please check the Author ID and try again!";
            }
            // otherwise delete author record from relevant tables
            else {
                stmt.executeUpdate("DELETE FROM Author WHERE authorid = " + authorID);
                return "SUCCESS: Author with author ID of " + authorID + " has been successfully deleted";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "AN UNEXPECTED ERROR HAS OCCURED";
        }
    }

    /*
     * Delete Book from records
     * 
     * @return
     * SUCCESS: Book with isbn of 1234 has been successfully deleted
     */
    public String deleteBook(int isbn) {
        try {
            // Create Statement
            Statement stmt = con.createStatement();
            // Check if book exist
            String checkBookExist = "SELECT * FROM book WHERE isbn = " + isbn;
            ResultSet rsBookExist = stmt.executeQuery(checkBookExist);
            // if not do nothing
            if (rsBookExist.next() == false) {
                return "CANNOT DELETE: Could not find book with ISBN of " + isbn
                        + " - Please check the ISBN and try again!";
            } else {
                // Check if book has copies on loan
                String checkBookOnLoan = "SELECT * FROM cust_book WHERE isbn=" + isbn;
                ResultSet rsBookOnLoan = stmt.executeQuery(checkBookOnLoan);
                // if so do nothing
                if (rsBookOnLoan.next()) {
                    return "CANNOT DELETE: Book with ISBN of " + isbn
                            + " cannot be deleted - loaned copies not returned";
                }
                // otherwise delete book record from relevant tables
                else {
                    stmt.executeUpdate("DELETE FROM book WHERE isbn = " + isbn);
                    return "SUCCESS: Book with ISBN of " + isbn + " has been successfully deleted";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "AN UNEXPECTED ERROR HAS OCCURED";
        }
    }
}