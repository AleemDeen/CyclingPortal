package cycling;

public class Checkpoint {
	private int id;
	private int stageId;
	private double location;
	private CheckpointType type;
	private double averageGradient;
	private double length;

	public Checkpoint(int id, int stageId, double location, CheckpointType type, double averageGradient,
			double length) {
		this.id = id;
		this.stageId = stageId;
		this.location = location;
		this.type = type;
		this.averageGradient = averageGradient;
		this.length = length;
	}

	// Getters and setters for attributes

	public Checkpoint(int stageId2, Double location2, CheckpointType type2, Double averageGradient2, Double length2) {
		this.stageId = stageId2;
		this.location = location2;
		this.type = type2;
		this.averageGradient = averageGradient2;
		this.length = length2;
		
	}

	public Checkpoint(int checkpointId, int stageId2, double location2, CheckpointType sprint) {
		this.id = checkpointId;
		this.stageId = stageId2;
		this.location = location2;
		this.type = sprint;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStageId() {
		return stageId;
	}

	public void setStageId(int stageId) {
		this.stageId = stageId;
	}

	public double getLocation() {
		return location;
	}

	public void setLocation(double location) {
		this.location = location;
	}

	public CheckpointType getType() {
		return type;
	}

	public void setType(CheckpointType type) {
		this.type = type;
	}

	public double getAverageGradient() {
		return averageGradient;
	}

	public void setAverageGradient(double averageGradient) {
		this.averageGradient = averageGradient;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}
}
