package com.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.pojo.Book;
import com.pojo.Subject;
import com.sun.xml.internal.ws.encoding.soap.DeserializationException;


public class SerializeUtil {
	

	// deserialize to Object from given file
	public static Object deserialize(String fileName) throws IOException,
			ClassNotFoundException {
		if(!Files.exists(Paths.get(fileName))){
			return null;
		}
		System.out.println(Paths.get(fileName).toAbsolutePath().toString());
		FileInputStream fis = new FileInputStream(fileName);
		if(fis!=null) {
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object obj = ois.readObject();
			ois.close();
			return obj;
		}
		return null;
		
	}

	// serialize the given object and save it to file
	public static void serialize(Object obj, String fileName)
			throws IOException {
		List<Object> serializeObjectLst = new ArrayList<>();
		try {
			Object oldDeserializeObj = deserialize(fileName);
			if(oldDeserializeObj!=null) {
				serializeObjectLst.addAll((Collection<? extends Object>)oldDeserializeObj);
			}
		}catch(Exception e) {
			e.printStackTrace();
			DeserializationException exception = new DeserializationException(e.getMessage());
			throw exception;
		}
		FileOutputStream fos = new FileOutputStream(fileName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		serializeObjectLst.add(obj);
		oos.writeObject(serializeObjectLst);
		fos.close();
	}

	public static Set<Book> searchBookById(List<Long> bookIds,String fileName) throws DeserializationException {
		try {
		List<Book> lstBooks = (List<Book>)deserialize(fileName);
		Set<Book> searchLst =  lstBooks.stream().filter(book->bookIds.contains(book.getBookId())).collect(Collectors.toSet());
		return searchLst;
		}catch(Exception e) {
			DeserializationException exception = new DeserializationException(e.getMessage());
			throw exception;
		}
	}
	
	public static Book searchBookById(long bookId,String fileName) throws DeserializationException {
		try {
		List<Book> lstBooks = (List<Book>)deserialize(fileName);
		Optional<Book> result = lstBooks.stream().filter(book->book.getBookId()==bookId).findAny();
		if(result.isPresent()) {
			return result.get();
		}
		}catch(Exception e) {
			DeserializationException exception = new DeserializationException(e.getMessage());
			throw exception;
		}
		return null;
	}
	
	public static Subject searchSubjectById(long subjectId,String fileName) throws DeserializationException {
		try {
		List<Subject> lstSubjects = (List<Subject>)deserialize(fileName);
		//System.out.println("test>>>>>>"+lstSubjects);
		Optional<Subject> result = lstSubjects.stream().filter(subject->subject.getSubjectId()==subjectId).findAny();
		if(result.isPresent()) {
			return result.get();
		}
		//System.out.println(lstSubjects.stream().peek(subject -> System.out.println("peek1 " + subject.getSubjectId())).filter(subject->subject.getSubjectId()==subjectId).findAny().isPresent());
		}catch(Exception e) {
			e.printStackTrace();
			DeserializationException exception = new DeserializationException(e.getMessage());
			throw exception;
		}
		return null;
	}
	
	public static boolean deleteBookById(long bookId,String fileName) {
		try {
		List<Book> lstBooks = (List<Book>)deserialize(fileName);
		Optional<Book> result = lstBooks.stream().filter(book->book.getBookId()==bookId).findAny();
		if(result!=null) {
			lstBooks.remove(result.get());
			emptyFile(FSDConstants.BOOK_FILE_NAME);
			deleteFile(FSDConstants.BOOK_FILE_NAME);
			serialize(lstBooks,FSDConstants.BOOK_FILE_NAME);
			return true;
		}
		}catch(Exception e) {
			DeserializationException exception = new DeserializationException(e.getMessage());
			throw exception;
		}
		return false;
	}
	
	public static boolean deleteSubjectById(long subjectId,String fileName)  {
		try {
			List<Subject> lstSubjects = (List<Subject>)deserialize(fileName);
			Optional<Subject> result = lstSubjects.stream().filter(subject->subject.getSubjectId()==subjectId).findAny();
			if(result!=null) {
				boolean removed= lstSubjects.remove(result.get());
				emptyFile(FSDConstants.SUBJECT_FILE_NAME);
				deleteFile(FSDConstants.SUBJECT_FILE_NAME);
				serialize(lstSubjects,FSDConstants.SUBJECT_FILE_NAME);
				return true;
			}
		}catch(Exception e) {
			DeserializationException exception = new DeserializationException(e.getMessage());
			throw exception;
		}

		return false;
	}
	
	public void listBooksAndSubject() {
		try {
		List<Book> lstBooks = (List<Book>)deserialize(FSDConstants.BOOK_FILE_NAME);
		List<Subject> lstSubjects = (List<Subject>)deserialize(FSDConstants.SUBJECT_FILE_NAME);
		System.out.println(lstBooks);
		System.out.println(lstSubjects);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<Book> listBooks() {
		try {
		List<Book> lstBooks = (List<Book>)deserialize(FSDConstants.BOOK_FILE_NAME);
		return lstBooks;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Subject> listSubjects() {
		try {
		List<Subject> lstSubjects = (List<Subject>)deserialize(FSDConstants.SUBJECT_FILE_NAME);
		return lstSubjects;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static void emptyFile(String filename) throws IOException {
	    OutputStream os = null;
	    try {
	      os = new FileOutputStream(filename);
	    } finally {
	      if (os != null) {
	    	 os.flush();
	        os.close();
	      }
	    }
	  }

	private static void deleteFile(String filename) {
	    File f = new File(filename);
	    if (f.delete()) {
	      System.out.println(filename + " deleted sucessfully...");
	    } else {
	      System.out.println(filename + " deletion failed!");
	    }
	  }
}
