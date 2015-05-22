package com.seandot7.facepp;
import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;

/**
 * @Description TODO
 * @Author Wang Qi
 * @Date May 7, 2015
 */

public class FaceppUtils {
	//replace api_key and api_secret here (note)
	static final HttpRequests httpRequests = new HttpRequests("7abfd7412b5a17caecef91c6f13f3af2", "kwO6Sz0DmdjuuZbj2oI-hfK7sRH2t6WD", true, true);

	static final String IMAGE_FILE = "/Users/sean_7/projects/FacePlusPlus/pics/messi_01.jpg";
	static final double RECOGNITION_THRESHOLD = 10.0;
	
	
	public static JSONObject createPerson(String personName) {
		System.out.println("\nperson/create: " + personName);
		JSONObject result = null;
		try {
			result = httpRequests.personCreate(new PostParameters().setPersonName(personName));
		} catch (FaceppParseException e) {
			e.printStackTrace();
		}
		System.out.println(result);
		return result;
	}
	
	public static JSONObject getPersonInfo(String personName) {
		System.out.println("\nperson/get_info: " + personName);
		JSONObject result = null;
		try {
			result = httpRequests.personGetInfo(new PostParameters().setPersonName(personName));
		} catch (FaceppParseException e) {
			e.printStackTrace();
		}
		System.out.println(result);
		return result;	
	}
	
	public static JSONObject deletePerson(String personName) {
		System.out.println("\nperson/delete: " + personName);
		JSONObject result = null;
		try {
			result = httpRequests.personDelete(new PostParameters().setPersonName(personName));
		} catch (FaceppParseException e) {
			e.printStackTrace();
		}
		System.out.println(result);
		return result;	
	}
	
	public static boolean deletePerson2(String personName) {
		JSONObject result = deletePerson(personName);
		try {
			if ((boolean) result.get("success"))
				return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static JSONObject detectFace(String imageFilePath) {
		// detection/detect
		System.out.println("\ndetection/detect: " + imageFilePath);
		JSONObject result = null;
		File imageFile = new File(imageFilePath);
		if (!imageFile.isFile())
			return null;
		try {
			result = httpRequests.detectionDetect(new PostParameters().setImg(imageFile).setMode("oneface"));
		} catch (FaceppParseException e2) {
			e2.printStackTrace();
		}
		return result;
	}
	
	public static JSONObject addFaceToPerson(String imageFilePath, String personName) {
		// detection/detect
		System.out.println("\ndetection/detect: " + imageFilePath);
		JSONObject result = detectFace(imageFilePath);
		if (result == null)
			return null;
		
		String faceId = null;
		try {
			faceId = result.getJSONArray("face").getJSONObject(0).getString("face_id");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		// person/add_face
		System.out.println("\nperson/add_face: " + imageFilePath + " to " + personName);
		try {
			result = httpRequests.personAddFace(new PostParameters().setFaceId(faceId).setPersonName(personName));
		} catch (FaceppParseException e) {
			e.printStackTrace();
		}
		System.out.println(result);
		return result;	
	}
	
	public static boolean addFaceToPerson2(String imageFilePath, String personName) {
		JSONObject result = addFaceToPerson(imageFilePath, personName);
		try {
			if ((boolean) result.get("success"))
				return true;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static JSONObject createGroup(String groupName) {
		System.out.println("\ngroup/create: " + groupName);
		JSONObject result = null;
		try {
			result = httpRequests.groupCreate(new PostParameters().setGroupName(groupName));
		} catch (FaceppParseException e) {
			e.printStackTrace();
		}
		System.out.println(result);
		return result;
	}
	
	public static JSONObject deleteGroup(String groupName) {
		System.out.println("\nperson/delete: " + groupName);
		JSONObject result = null;
		try {
			result = httpRequests.groupDelete(new PostParameters().setGroupName(groupName));
		} catch (FaceppParseException e) {
			e.printStackTrace();
		}
		System.out.println(result);
		return result;	
	}
	
	public static JSONObject getGroupInfo(String groupName) {
		System.out.println("\ngroup/get_info: " + groupName);
		JSONObject result = null;
		try {
			result = httpRequests.groupGetInfo(new PostParameters().setGroupName(groupName));
		} catch (FaceppParseException e) {
			e.printStackTrace();
		}
		System.out.println(result);
		return result;	
	}
	
	public static JSONObject addPersonToGroup(String personName, String groupName) {
		System.out.println("\ngroup/add_person: " + personName + " to " + groupName);
		JSONObject result = null;
		try {
			result = httpRequests.groupAddPerson(new PostParameters().setPersonName(personName).setGroupName(groupName));
		} catch (FaceppParseException e) {
			e.printStackTrace();
		}
		System.out.println(result);
		return result;	
	}
	
	public static JSONObject getPersonList() {
		System.out.println("\ninfo/get_person_list");
		JSONObject result = null;
		try {
			result = httpRequests.infoGetPersonList();
		} catch (FaceppParseException e) {
			e.printStackTrace();
		}
		System.out.println(result);
		return result;
	}
	
	public static ArrayList<String> getPersonList2() {
		ArrayList<String> result = new ArrayList<String>();
		JSONObject tempResult = getPersonList();
		try {
			JSONArray people = tempResult.getJSONArray("person");
			for (int i=0; i<people.length(); ++i) {
				result.add(people.getJSONObject(i).getString("person_name"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static JSONObject getGroupList() {
		System.out.println("\ninfo/get_group_list");
		JSONObject result = null;
		try {
			result = httpRequests.infoGetGroupList();
		} catch (FaceppParseException e) {
			e.printStackTrace();
		}
		System.out.println(result);
		return result;
	}
	
	public static String trainIdentify(String groupName) {
		System.out.println("\ntrain/identify");
		JSONObject result = null;
		String sessionId = null;
		try {
			result = httpRequests.trainIdentify(new PostParameters().setGroupName(groupName));
			sessionId = result.getString("session_id");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(result);
		return sessionId;
	}
	
	public static String getTraninigStatus(String sessionId) {
		System.out.println("\ninfo/get_session");
		JSONObject result = null;
		String status = null;
		try {
			result = httpRequests.infoGetSession(new PostParameters().setSessionId(sessionId));
			status = result.getString("status");

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(result);
		return status;
		
	}
	
	public static JSONObject recoginizeFace(String imageFilePath, String groupName) {
		System.out.println("\nrecognition/identify");
		JSONObject result = null;
		try {
			result = httpRequests.recognitionIdentify(new PostParameters().setImg(new File(imageFilePath)).setGroupName(groupName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(result);
		return result;
	}
	
	// wrapper
	public static class Pair {
		String first = null;
		Double second = null;
		
		public Pair(String name, double confidence) {
			this.first = name;
			this.second = confidence;
		}
		public String getFirst() {
			return first;
		}
		public void setFirst(String first) {
			this.first = first;
		}
		public Double getSecond() {
			return second;
		}
		public void setSecond(Double second) {
			this.second = second;
		}
		
	}
	
	public static Pair recoginizeFace2(String imageFilePath, String groupName) {
		JSONObject result = recoginizeFace(imageFilePath, groupName);
		try {
			JSONArray candidates = result.getJSONArray("face").getJSONObject(0).getJSONArray("candidate");
			double confidence = 0.0;
			String name = "";
			for (int i=0; i<candidates.length(); ++i) {
				JSONObject person = candidates.getJSONObject(i);
				double tempConfidence = person.getDouble("confidence");
				if (tempConfidence > confidence) {
					confidence = tempConfidence;
					name = person.getString("person_name");
				}
			}
			System.out.println("It must be: " + name + " with confidence " + confidence);
			if (confidence < RECOGNITION_THRESHOLD)
				return null;
			else
				return new Pair(name, confidence);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static void display(String imageFilePath) {
		File imageFile = new File(imageFilePath);
		File landmarkedImageFile = new File(imageFile.getParent() + "/" + "marked_" + imageFile.getName());
		ImageUtils imageContext = new ImageUtils(imageFile.getAbsolutePath(), landmarkedImageFile.getAbsolutePath());
		System.out.println(imageFile.getParent());

		JSONObject result = null;
		// detection/detect
		result = detectFace(imageFilePath);
		
		try {
			String faceId = result.getJSONArray("face").getJSONObject(0).getString("face_id");
			JSONObject facePosition = result.getJSONArray("face").getJSONObject(0).getJSONObject("position");
			double imageWidth = result.getDouble("img_width");
			double imageHeight = result.getDouble("img_height");
			double faceWidth = imageWidth * facePosition.getDouble("width") / 100;
			double faceHeight = imageHeight * facePosition.getDouble("height") / 100;
			double x = imageWidth * facePosition.getJSONObject("center").getDouble("x") / 100 - faceWidth / 2;
			double y = imageHeight * facePosition.getJSONObject("center").getDouble("y") / 100 - faceHeight / 2;
			imageContext.drawRectangle(x, y, faceWidth, faceHeight);
			
			// detection/landmark
			result = httpRequests.detectionLandmark(new PostParameters().setFaceId(faceId));
			System.out.println(result);
			JSONObject landmarks = result.getJSONArray("result").getJSONObject(0).getJSONObject("landmark");

			for (Object key : landmarks.keySet()) {
				try {
					JSONObject point = landmarks.getJSONObject((String) key);
					double tempX = imageWidth * point.getDouble("x") / 100;
					double tempY = imageHeight * point.getDouble("y") / 100;
					imageContext.drawPoint(tempX, tempY, 2);
					
				} catch(Exception e) {
					e.printStackTrace();
				}
				
			}
			
			imageContext.write();
			imageContext.display();	
			
		} catch(Exception e) {
			e.printStackTrace();
		}

	

	}
	
	public static void main(String[] args) {
		
		try {
//			display(IMAGE_FILE);
			
//			deletePerson("Messi");
//			addFaceToPerson(IMAGE_FILE, "Messi");
//			getPersonInfo("Messi");
//			createGroup("test_group");
//			deleteGroup("test_group");
//			getGroupInfo("test_group");
			//info/get_person_list
			System.out.println("\ninfo/get_person_list");
			System.out.println(httpRequests.infoGetPersonList());
			//info/get_group_list
			System.out.println("\ninfo/get_group_list");
			System.out.println(httpRequests.infoGetGroupList());	
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
