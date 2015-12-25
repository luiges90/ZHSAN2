package com.zhsan.gameobject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.badlogic.gdx.files.FileHandle;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.zhsan.common.exception.FileReadException;
import com.zhsan.common.exception.FileWriteException;
import com.zhsan.gamecomponents.GlobalStrings;

/**
 * 
 * @author liujianwei
 *
 */
public class Family implements GameObject {

	public static final String SAVE_FILE = "Family.csv";

	private int familyId;

	private String name;

	private Node headerNode;

	private GameScenario scenario;

	public Family(int id, GameScenario scen) {
		this.familyId = id;
		this.scenario = scen;
	}

	public static void main(String[] args) {
		Family f = new Family(1, null);

		GameObjectList<Family> list = new GameObjectList<>();
		// first
		f.addMember(1, -1);

		f.addMember(11, 1);
		f.addMember(12, 1);
		f.addMember(13, 1);

		f.addMember(111, 11);
		f.addMember(112, 11);

		f.addMember(121, 12);
		f.addMember(122, 12);

		f.addMember(1211, 121);
		f.addMember(1212, 121);

		f.addMember(12121, 1212);

		list.add(f);

		// Family.toCSV(root, list);

		System.out.println(f.toCSV());

	}

	public static final GameObjectList<Family> fromCSV(FileHandle root, @NotNull GameScenario scen) {

		FileHandle f = root.child(SAVE_FILE);
		GameObjectList<Family> result = new GameObjectList<>();
		try (CSVReader reader = new CSVReader(new InputStreamReader(f.read(), "UTF-8"))) {
			String[] line;
			int index = 0;
			Family family = null;
			Node node = null;
			while ((line = reader.readNext()) != null) {
				index++;
				if (index == 1)
					continue; // skip first line.

				family = new Family(Integer.parseInt(line[0]), scen);
				for (int i = 1; i < line.length; i++) {
					node = Node.fromCSV(line[1]);
					family.addMember(node.getPersonId(), node.getParentId());
				}

				result.add(family);
			}
		} catch (IOException e) {
			throw new FileReadException(f.path(), e);
		}

		return result;
	}

	public static final void toCSV(FileHandle root, GameObjectList<Family> data) {
		FileHandle f = root.child(SAVE_FILE);
		try (CSVWriter writer = new CSVWriter(f.writer(false, "UTF-8"))) {
			writer.writeNext(GlobalStrings.getString(GlobalStrings.Keys.FAMILY_SAVE_HEADER).split(","));
			String[] values = null;
			for (Family d : data) {
				values = d.toCSV().split(",");
				writer.writeNext(values);
			}
		} catch (IOException e) {
			throw new FileWriteException(f.path(), e);
		}
	}

	/**
	 * add family member
	 * 
	 * @param id
	 *            the current person id
	 * @param parentId
	 *            the current person parent id
	 */
	public void addMember(int id, int parentId) {

		if (null == headerNode) {

			headerNode = new Node();
			headerNode.setPersonId(id);
			headerNode.setParentId(-1);
			headerNode.setGeneration(1);
			return;
		} else {

			Node child = new Node();
			child.setPersonId(id);

			Node parent = getNode(parentId, headerNode);
			if (null != parent) {
				parent.addChild(child);
			}
		}
	}

	public GameObjectList<Person> getChildren(int id) {
		Node node = getNode(id, headerNode);

		GameObjectList<Person> list = new GameObjectList<Person>();
		if (null != node) {
			Person p = null;
			List<Node> children = node.getChildren();
			for (Node child : children) {
				p = this.scenario.getPerson(child.getPersonId());
				if (null != p)
					list.add(p);
			}
		}

		return list;
	}

	public Node getNode(int id) {
		return getNode(id, headerNode);
	}

	private Node getNode(int id, Node header) {
		if (header.getPersonId() == id) {
			return header;
		}

		List<Node> children = header.getChildren();

		for (Node child : children) {
			if (child.getPersonId() == id) {
				return child;
			}
			Node ret = getNode(id, child);
			if (null != ret) {
				return ret;
			}
		}

		return null;
	}

	public int getAncestorId() {

		if (null != headerNode) {
			return headerNode.getPersonId();
		}
		return -1;
	}

	public Node getHeaderNode() {
		return headerNode;
	}

	public String toCSV() {
		StringBuilder sb = new StringBuilder();
		sb.append(familyId + ",");
		sb.append(headerNode.toCSV());

		return sb.toString();
	}

	@Override
	public String toString() {
		return "Family [familyId=" + familyId + ", headerNode=" + headerNode.toString() + "]";
	}

	/**
	 * 
	 * @author liujianwei
	 *
	 */
	public static class Node {

		private int personId;

		private int generation;

		private int parentId;

		private List<Node> children = new ArrayList<>();

		public void addChild(Node node) {
			node.setParentId(this.personId);
			node.setGeneration(this.generation + 1);
			children.add(node);
		}

		public int getParentId() {
			return parentId;
		}

		public void setParentId(int parentId) {
			this.parentId = parentId;
		}

		public int getPersonId() {
			return personId;
		}

		public List<Node> getChildren() {
			return children;
		}

		public void setPersonId(int personId) {
			this.personId = personId;
		}

		public int getGeneration() {
			return generation;
		}

		public void setGeneration(int generation) {
			this.generation = generation;
		}

		public static final Node fromCSV(String field) {
			Node node = new Node();
			String[] values = field.split(" ");
			node.setPersonId(Integer.parseInt(values[0]));
			node.setParentId(Integer.parseInt(values[1]));
			node.setGeneration(Integer.parseInt(values[2]));

			return node;
		}

		public final String toCSV() {
			StringBuilder sb = new StringBuilder();

			sb.append(personId + " " + parentId + " " + generation + ",");

			for (Node child : children) {
				sb.append(child.toCSV());
			}

			return sb.toString();
		}

		@Override
		public String toString() {

			StringBuilder sb = new StringBuilder();
			sb.append("Node [personId=" + personId + ", generation=" + generation + ", parentId=" + parentId + "]");

			for (Node child : children) {
				sb.append(child.toString());
			}

			return sb.toString();
		}

	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getId() {
		return this.familyId;
	}

	@Override
	public String getAiTags() {
		return "";
	}

	@Override
	public GameObject setAiTags(String aiTags) {
		return null;
	}

}
