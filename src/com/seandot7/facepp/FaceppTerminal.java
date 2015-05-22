package com.seandot7.facepp;

import java.util.ArrayList;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Description TODO
 * @Author Wang Qi
 * @Date May 7, 2015
 */



public class FaceppTerminal {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("\ncommands:"
							+ "\n\t display <filepath>"
							+ "\n\t create_person <person_name>"
							+ "\n\t delete_person <person_name>"
							+ "\n\t get_person_info <person_name>"
							+ "\n\t add_face_to_person <face_file_path> <person_name>"
							+ "\n\t create_group <group_name>"
							+ "\n\t delete_group <group_name>"
							+ "\n\t get_group_info <group_name>"
							+ "\n\t add_person_to_group <person_name> <group_name>"
							+ "\n\t get_person_list"
							+ "\n\t get_group_list"
							+ "\n\t train_identify <group_name>"
							+ "\n\t get_training_status <session_id>"
							+ "\n\t recognize_face <face_file_path> <group_name>"
		);
		while (true) {
			System.out.print("cmd: ");
			String rawCommand = scanner.nextLine().trim();
			String[] tempStrings = rawCommand.split(" ");
			String cmd = tempStrings[0].toLowerCase();
			ArrayList<String> params = new ArrayList<String>();
			for (int i=1; i<tempStrings.length; ++i)
				params.add(tempStrings[i]);
				
			if ("quit".equals(cmd))
				break;
			
			if ("display".equals(cmd) && params.size()==1) {
				FaceppUtils.display(params.get(0));
			}
			
			if ("create_person".equals(cmd) && params.size()==1) {
				FaceppUtils.createPerson(params.get(0));
			}
			
			if ("delete_person".equals(cmd) && params.size()==1) {
				FaceppUtils.deletePerson(params.get(0));
			}
			
			if ("get_person_info".equals(cmd) && params.size()==1) {
				FaceppUtils.getPersonInfo(params.get(0));
			}
			
			if ("add_face_to_person".equals(cmd) && params.size()==2) {
				FaceppUtils.addFaceToPerson(params.get(0), params.get(1));
			}
			
			if ("create_group".equals(cmd) && params.size()==1) {
				FaceppUtils.createGroup(params.get(0));
			}
			
			if ("delete_group".equals(cmd) && params.size()==1) {
				FaceppUtils.deleteGroup(params.get(0));
			}
			
			if ("get_group_info".equals(cmd) && params.size()==1) {
				FaceppUtils.getGroupInfo(params.get(0));
			}
			
			if ("add_person_to_group".equals(cmd) && params.size()==2) {
				FaceppUtils.addPersonToGroup(params.get(0), params.get(1));
			}
			
			
			if ("get_person_list".equals(cmd)) {
				FaceppUtils.getPersonList();
			}
			
			if ("get_group_list".equals(cmd)) {
				FaceppUtils.getGroupList();
			}
			
			if ("train_identify".equals(cmd) && params.size()==1) {
				FaceppUtils.trainIdentify(params.get(0));
			}
			
			if ("get_training_status".equals(cmd) && params.size()==1) {
				FaceppUtils.getTraninigStatus(params.get(0));
			}
			
			if ("recognize_face".equals(cmd) && params.size()==2) {
				JSONObject result = FaceppUtils.recoginizeFace(params.get(0), params.get(1));
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
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
			
			
			
		}
	}

}
