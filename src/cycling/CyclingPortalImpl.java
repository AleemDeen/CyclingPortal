package cycling;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * BadCyclingPortal is a minimally compiling, but non-functioning implementor of
 * the CyclingPortal interface.
 * 
 * @author Diogo Pacheco
 * @version 2.0
 *
 */
public class CyclingPortalImpl implements CyclingPortal {
	private Map<Integer, Race> races = new HashMap<>();
	private AtomicInteger nextRaceId = new AtomicInteger(1);
	private Map<Integer, Team> teams = new HashMap<>();
	private AtomicInteger nextTeamId = new AtomicInteger(1);
	private Map<Integer, Stage> stages = new HashMap<>();
	private AtomicInteger nextStageId = new AtomicInteger(1);
	private List<Race> races1;
	private AtomicInteger nextCheckpointId = new AtomicInteger(1);
	private Map<Integer, Rider> riders = new HashMap<>();
	private AtomicInteger nextRiderId = new AtomicInteger(1);

	@Override
	public int[] getRaceIds() {
		// Return an empty array instead of null
		return races.keySet().stream().mapToInt(Integer::intValue).toArray();
	}

	@Override
	public int createRace(String name, String description) throws IllegalNameException, InvalidNameException {
		if (name == null || name.isEmpty() || name.trim().isEmpty()) {
			throw new InvalidNameException("Name cannot be null, empty, or contain only whitespace.");
		}

		// Check if the name has more than 30 characters
		if (name.length() > 30) {
			throw new InvalidNameException("Name cannot exceed 30 characters.");
		}

		// Check if the name already exists in the platform
		if (races.values().stream().anyMatch(race -> race.getName().equals(name))) {
			throw new IllegalNameException("Race name already exists in the platform.");
		}

		// Generate a unique ID for the race
		int raceId = nextRaceId.getAndIncrement();

		// Create a new Race object and store it in the races map
		races.put(raceId, new Race(raceId, name, description));

		// Return the unique ID of the created race
		return raceId;
	}

	@Override
	public String viewRaceDetails(int raceId) throws IDNotRecognisedException {
		// Check if the raceId exists in the platform
		if (!races.containsKey(raceId)) {
			throw new IDNotRecognisedException("Race ID not recognised.");
		}

		// Get the race details
		Race race = races.get(raceId);

		// Calculate the number of stages and total length for the given raceId
		int numberOfStages = 0;
		double totalLength = 0;
		for (Stage stage : stages.values()) {
			if (stage.getRaceId() == raceId) {
				numberOfStages++;
				totalLength += stage.getLength();
			}
		}

		// Format the race details
		StringBuilder detailsBuilder = new StringBuilder();
		detailsBuilder.append("Race ID: ").append(raceId).append("\n");
		detailsBuilder.append("Name: ").append(race.getName()).append("\n");
		detailsBuilder.append("Description: ").append(race.getDescription()).append("\n");
		detailsBuilder.append("Number of Stages: ").append(numberOfStages).append("\n");
		detailsBuilder.append("Total Length: ").append(totalLength).append(" km\n");

		return detailsBuilder.toString();
	}

	@Override
	public void removeRaceById(int raceId) throws IDNotRecognisedException {
		// Check if the raceId exists in the platform
		if (!races.containsKey(raceId)) {
			throw new IDNotRecognisedException("Race ID not recognised.");
		}
		// Remove the race and its related information
		races.remove(raceId);

	}

	@Override
	public int getNumberOfStages(int raceId) throws IDNotRecognisedException {
		// Check if the raceId exists in the platform
		if (!races.containsKey(raceId)) {
			throw new IDNotRecognisedException("Race ID not recognised.");
		}
		// Count the number of stages for the specified raceId
		int numberOfStages = 0;
		for (Stage stage : stages.values()) {
			if (stage.getRaceId() == raceId) {
				numberOfStages++;
			}
		}

		return numberOfStages;

	}

	@Override
	public int addStageToRace(int raceId, String stageName, String description, double length, LocalDateTime startTime,
			StageType type)
			throws IDNotRecognisedException, IllegalNameException, InvalidNameException, InvalidLengthException {

		// Check if the raceId exists in the platform
		if (!races.containsKey(raceId)) {
			throw new IDNotRecognisedException("Race ID not recognised.");
		}

		// Check if the stageName is null or empty
		if (stageName == null || stageName.isEmpty() || stageName.trim().isEmpty()) {
			throw new InvalidNameException("Stage name cannot be null, empty, or contain only whitespace.");
		}

		// Check if the stageName has more than 30 characters
		if (stageName.length() > 30) {
			throw new InvalidNameException("Stage name cannot exceed 30 characters.");
		}
		// Check if the length is less than 5km
		if (length < 5) {
			throw new InvalidLengthException("Stage length cannot be less than 5km.");
		}

		// Generate a unique ID for the stage
		int stageId = nextStageId.getAndIncrement();
		// Create a new Stage object and store it in the stages map
		stages.put(stageId, new Stage(stageId, raceId, stageName, description, length, startTime, type));

		// Return the unique ID of the created stage
		return stageId;

	}

	@Override
	public int[] getRaceStages(int raceId) throws IDNotRecognisedException {
		// Check if the raceId exists
		if (!races.containsKey(raceId)) {
			throw new IDNotRecognisedException("Race ID not recognised.");
		}
		// Get the list of stage IDs for the given race
		List<Integer> stageIds = new ArrayList<>();
		for (Stage stage : stages.values()) {
			if (stage.getRaceId() == raceId) {
				stageIds.add(stage.getId());
			}
		}
		// Convert the list of stage IDs to an array
		int[] result = new int[stageIds.size()];
		for (int i = 0; i < stageIds.size(); i++) {
			result[i] = stageIds.get(i);
		}

		return result;
	}

	@Override
	public double getStageLength(int stageId) throws IDNotRecognisedException {
		// Check if the stageId exists in the platform
		if (!stages.containsKey(stageId)) {
			throw new IDNotRecognisedException("Stage ID not recognised.");
		}
		// Retrieve the stage with the given ID
		Stage stage = stages.get(stageId);

		// Return the length of the stage
		return stage.getLength();

	}

	@Override
	public void removeStageById(int stageId) throws IDNotRecognisedException {
		// Check if the stageId exists in the platform
		if (!stages.containsKey(stageId)) {
			throw new IDNotRecognisedException("Stage ID not recognised.");
		}

		// Remove the stage with the given ID
		stages.remove(stageId);

	}

	@Override
	public int addCategorizedClimbToStage(int stageId, Double location, CheckpointType type, Double averageGradient,
			Double length) throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException,
			InvalidStageTypeException {
		// Check if the stageId exists in the platform
		if (!stages.containsKey(stageId)) {
			throw new IDNotRecognisedException("Stage ID not recognised.");
		}

		// Get the stage from the map
		Stage stage = stages.get(stageId);

		// Check if the stage is in a valid state for adding a climb checkpoint
		if (stage.getState() == StageState.WAITING_FOR_RESULTS) {
			throw new InvalidStageStateException("Stage is in 'waiting for results' state and cannot be modified.");
		}

		// Check if the stage type allows adding a climb checkpoint
		if (stage.getType() == StageType.TT) {
			throw new InvalidStageTypeException("Time-trial stages cannot contain any checkpoint.");
		}

		// Check if the location is within the stage length bounds
		if (location < 0 || location > stage.getLength()) {
			throw new InvalidLocationException("Location is out of bounds of the stage length.");
		}

		// Initialize checkpoints list if it's null
		if (stage.getCheckpoints() == null) {
			stage.setCheckpoints(new ArrayList<>());
		}
		// Generate a unique ID for the checkpoint
		int checkpointId = nextCheckpointId.getAndIncrement();

		// Create a new Checkpoint object and add it to the stage's checkpoints
		Checkpoint checkpoint = new Checkpoint(checkpointId, stageId, location, type, averageGradient, length);
		stage.addCheckpoint(checkpoint);

		// Return the ID of the created checkpoint
		return checkpointId;
	}

	@Override
	public int addIntermediateSprintToStage(int stageId, double location) throws IDNotRecognisedException,
			InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
		// Check if the stageId exists in the platform
		if (!stages.containsKey(stageId)) {
			throw new IDNotRecognisedException("Stage ID not recognised.");
		}

		// Get the stage
		Stage stage = stages.get(stageId);

		// Check if the stage is in a valid state for adding an intermediate sprint
		if (stage.getState() == StageState.WAITING_FOR_RESULTS) {
			throw new InvalidStageStateException("Stage is in 'waiting for results' state and cannot be modified.");
		}
		// Check if the stage type allows adding an intermediate sprint
		if (stage.getType() == StageType.TT) {
			throw new InvalidStageTypeException("Time-trial stages cannot contain any checkpoint.");
		}

		// Check if the location is within the stage length bounds
		if (location < 0 || location > stage.getLength()) {
			throw new InvalidLocationException("Location is out of bounds of the stage length.");
		}
		// Generate a unique ID for the intermediate sprint checkpoint
		int checkpointId = nextCheckpointId.getAndIncrement();

		// Create a new Checkpoint object for the intermediate sprint and add it to the
		// stage's checkpoints
		Checkpoint intermediateSprint = new Checkpoint(checkpointId, stageId, location, CheckpointType.SPRINT);
		stage.addCheckpoint(intermediateSprint);

		// Return the ID of the created intermediate sprint checkpoint
		return checkpointId;
	}

	@Override
	public void removeCheckpoint(int checkpointId) throws IDNotRecognisedException, InvalidStageStateException {
		// Check if the checkpointId exists in any of the stages' checkpoints
		boolean checkpointFound = false;
		for (Stage stage : stages.values()) {
			List<Checkpoint> checkpoints = stage.getCheckpoints();
			for (Checkpoint checkpoint : checkpoints) {
				if (checkpoint.getId() == checkpointId) {
					checkpointFound = true;
					// Check if the stage is in a valid state for removing a checkpoint
					if (stage.getState() == StageState.WAITING_FOR_RESULTS) {
						throw new InvalidStageStateException(
								"Stage is in 'waiting for results' state and cannot be modified.");
					}
					// Remove the checkpoint from the stage
					checkpoints.remove(checkpoint);
					break;
				}
			}
			if (checkpointFound) {
				break;
			}
		}

		// If checkpoint not found, throw IDNotRecognisedException
		if (!checkpointFound) {
			throw new IDNotRecognisedException("Checkpoint ID not recognised.");
		}
	}

	@Override
	public void concludeStagePreparation(int stageId) throws IDNotRecognisedException, InvalidStageStateException {
		// Check if the stageId exists in the platform
		if (!stages.containsKey(stageId)) {
			throw new IDNotRecognisedException("Stage ID not recognised.");
		}

		// Get the stage from the map
		Stage stage = stages.get(stageId);

		// Check if the stage is already in "waiting for results" state
		if (stage.getState() == StageState.WAITING_FOR_RESULTS) {
			throw new InvalidStageStateException("Stage is already in 'waiting for results' state.");
		}
		// Set the stage state to "waiting for results"
		stage.setState(StageState.WAITING_FOR_RESULTS);

	}

	@Override
	public int[] getStageCheckpoints(int stageId) throws IDNotRecognisedException {
		// Check if the stageId exists in the platform
		if (!stages.containsKey(stageId)) {
			throw new IDNotRecognisedException("Stage ID not recognised.");
		}
		// Get the stage from the map
		Stage stage = stages.get(stageId);

		// Get the list of checkpoints from the stage
		List<Checkpoint> checkpoints = stage.getCheckpoints();

		// Create an array to store the checkpoint IDs
		int[] checkpointIds = new int[checkpoints.size()];

		// Iterate through the checkpoints and store their IDs in the array
		for (int i = 0; i < checkpoints.size(); i++) {
			checkpointIds[i] = checkpoints.get(i).getId();
		}

		// Return the array of checkpoint IDs
		return checkpointIds;
	}

	@Override
	public int createTeam(String name, String description) throws IllegalNameException, InvalidNameException {
		// Check if the name is null or empty
		if (name == null || name.isEmpty() || name.trim().isEmpty()) {
			throw new InvalidNameException("Name cannot be null, empty, or contain only whitespace.");
		}

		// Check if the name has more than 30 characters
		if (name.length() > 30) {
			throw new InvalidNameException("Name cannot exceed 30 characters.");
		}

		// Check if the name already exists in the platform
		if (teams.values().stream().anyMatch(team -> team.getName().equals(name))) {
			throw new IllegalNameException("Team name already exists in the platform.");
		}

		// Generate a unique ID for the team
		int teamId = nextTeamId.getAndIncrement();

		// Create a new Team object and store it in the teams map
		teams.put(teamId, new Team(teamId, name, description));

		// Return the unique ID of the created team
		return teamId;
	}

	@Override
	public void removeTeam(int teamId) throws IDNotRecognisedException {
		// Check if the teamId exists in the teams map
		if (!teams.containsKey(teamId)) {
			throw new IDNotRecognisedException("Team ID not recognised.");
		}

		// Remove the team from the teams map
		teams.remove(teamId);

	}

	@Override
	public int[] getTeams() {
		return teams.keySet().stream().mapToInt(Integer::intValue).toArray();
	}

	@Override
	public int[] getTeamRiders(int teamId) throws IDNotRecognisedException {
		// Get Team
		Team team = teams.get(teamId);
		if (team == null) {
			throw new IDNotRecognisedException("Team ID not recognised.");
		}
		List<Rider> riders = team.getRiders();
		int[] riderIds = new int[riders.size()];
		for (int i = 0; i < riders.size(); i++) {
			riderIds[i] = riders.get(i).getId();
		}

		return riderIds;
	}

	@Override
	public int createRider(int teamID, String name, int yearOfBirth)
			throws IDNotRecognisedException, IllegalArgumentException {
		// Check if the teamID exists in the system
		if (!teams.containsKey(teamID)) {
			throw new IDNotRecognisedException("Team ID not recognised.");
		}

		// Validate input parameters
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Rider name cannot be null or empty.");
		}
		if (yearOfBirth < 1900) {
			throw new IllegalArgumentException("Year of birth cannot be less than 1900.");
		}

		// Generate a unique ID for the rider
		int riderID = nextRiderId.getAndIncrement();

		// Create a new Rider object
		Rider rider = new Rider(riderID, teamID, name, yearOfBirth);

		// Add the rider to the team's list of riders
		Team team = teams.get(teamID);
		team.addRider(rider);

		// Return the ID of the created rider
		return riderID;
	}

	@Override
	public void removeRider(int riderId) throws IDNotRecognisedException {
		// Search for the rider in all teams and remove them if found
		for (Team team : teams.values()) {
			if (team.containsRider(riderId)) {
				team.removeRider(riderId);
				return; // Exit the method after removing the rider
			}
		}

		// If the rider was not found in any team, throw an IDNotRecognisedException
		throw new IDNotRecognisedException("Rider ID not recognised.");

	}

	@Override
	public void registerRiderResultsInStage(int stageId, int riderId, LocalTime... checkpoints)
			throws IDNotRecognisedException, DuplicatedResultException, InvalidCheckpointTimesException,
			InvalidStageStateException {
		// Check if the stageId exists in the system
		Stage stage = stages.get(stageId);
		if (stage == null) {
			throw new IDNotRecognisedException("Stage ID not recognised.");
		}
		// Check if the riderId exists in the system
		Team team = teams.get(riderId);
		if (team == null) {
			throw new IDNotRecognisedException("Rider ID not recognised.");
		}

		// Check if the stage is in a valid state for registering results
		if (!stage.getState().equals(StageState.WAITING_FOR_RESULTS)) {
			throw new InvalidStageStateException("Stage is not in 'waiting for results' state.");
		}

		// Check if the rider already has a result for this stage
		if (stage.hasResultForRider(riderId)) {
			throw new DuplicatedResultException("Rider already has a result for this stage.");
		}
		// Check if the length of checkpointTimes is equal to n+2
		int numCheckpoints = stage.getCheckpoints().size();
		if (checkpoints.length != numCheckpoints + 2) {
			throw new InvalidCheckpointTimesException("Invalid number of checkpoint times.");
		}
		// Add the result to the stage
		Result result = new Result(riderId, checkpoints);
		stage.addResult(result);
	}

	@Override
	public LocalTime[] getRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
		// Check if the stageId and riderId exist in the system
		if (!stages.containsKey(stageId)) {
			throw new IDNotRecognisedException("Stage ID not recognised.");
		}

		Stage stage = stages.get(stageId);

		// Get the rider's results directly from the stage
		Map<Integer, Result> results = stage.getResults();

		if (!results.containsKey(riderId)) {
			return new LocalTime[0];
		}

		Result result = results.get(riderId);

		// Calculate the elapsed time
		LocalTime[] checkpointTimes = result.getCheckpointTimes();
		LocalTime startTime = checkpointTimes[0];
		LocalTime finishTime = checkpointTimes[checkpointTimes.length - 1];
		LocalTime elapsedTime = finishTime.minusHours(startTime.getHour()).minusMinutes(startTime.getMinute());

		// Combine the checkpoint times and the total elapsed time
		LocalTime[] riderResults = new LocalTime[checkpointTimes.length + 1];
		System.arraycopy(checkpointTimes, 0, riderResults, 0, checkpointTimes.length);
		riderResults[checkpointTimes.length] = elapsedTime;

		return riderResults;
	}

	@Override
	public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId) throws IDNotRecognisedException {
		// Check if the stageId and riderId exist in the system
		if (!stages.containsKey(stageId)) {
			throw new IDNotRecognisedException("Stage ID not recognised.");
		}

		Stage stage = stages.get(stageId);

		// Get the rider's results directly from the stage
		Map<Integer, Result> results = stage.getResults();

		if (!results.containsKey(riderId)) {
			return null; // Return null if no result for the rider in the stage
		}

		Result result = results.get(riderId);

		// Calculate the adjusted elapsed time based on the specified criteria
		LocalTime[] checkpointTimes = result.getCheckpointTimes();
		LocalTime startTime = checkpointTimes[0];
		LocalTime finishTime = checkpointTimes[checkpointTimes.length - 1];

		if (stage.getType() != StageType.TT) {
			// Calculate adjusted elapsed time for non-time-trial stages
			LocalTime adjustedElapsedTime = finishTime.minusHours(startTime.getHour())
					.minusMinutes(startTime.getMinute());

			// Additional adjustment if needed (less than 1 second gap with previous rider)
			// Example logic, adjust as per your requirements
			if (isAdjustedTimeNeeded(result, riderId)) {
				adjustedElapsedTime = adjustElapsedTime(adjustedElapsedTime, stage, result);
			}

			return adjustedElapsedTime;
		} else {
			// For time-trial stages, return the regular elapsed time
			return finishTime.minusHours(startTime.getHour()).minusMinutes(startTime.getMinute());
		}
	}

	@Override
	public void deleteRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
		// Check if the stageId and riderId exist in the system
		if (!stages.containsKey(stageId)) {
			throw new IDNotRecognisedException("Stage ID not recognised.");
		}

		Stage stage = stages.get(stageId);

		// Get the rider's results directly from the stage
		Map<Integer, Result> results = stage.getResults();

		if (!results.containsKey(riderId)) {
			throw new IDNotRecognisedException("Rider ID not recognised in this stage.");
		}
		// Remove the rider's result from the stage
		results.remove(riderId);

	}

	@Override
	public int[] getRidersRankInStage(int stageId) throws IDNotRecognisedException {
		// Check if the stageId exists in the system
		Stage stage = stages.get(stageId);
		if (stage == null) {
			throw new IDNotRecognisedException("Stage ID not recognised.");
		}

		// Check if there are results for the stage
		if (stage.getResults().isEmpty()) {
			return new int[0]; // No results for the stage, return an empty list
		}
		// Sort riders by their elapsed time
		List<Integer> sortedRiders = new ArrayList<>(stage.getResults().keySet());
		Collections.sort(sortedRiders, (rider1, rider2) -> {
			LocalTime time1 = stage.getResults().get(rider1).getElapsedTime();
			LocalTime time2 = stage.getResults().get(rider2).getElapsedTime();
			return time1.compareTo(time2);
		});

		// Convert the list of sorted riders to an array
		int[] resultArray = new int[sortedRiders.size()];
		for (int i = 0; i < sortedRiders.size(); i++) {
			resultArray[i] = sortedRiders.get(i);
		}

		return resultArray;
	}

	@Override
	public LocalTime[] getRankedAdjustedElapsedTimesInStage(int stageId) throws IDNotRecognisedException {
		Stage stage = stages.get(stageId);
		if (stage == null) {
			throw new IDNotRecognisedException("Stage ID not recognised.");
		}

		List<Result> results = new ArrayList<>(stage.getResults().values());
		results.sort(Comparator.comparing(Result::getAdjustedElapsedTime));

		LocalTime[] rankedTimes = new LocalTime[results.size()];
		for (int i = 0; i < results.size(); i++) {
			rankedTimes[i] = results.get(i).getAdjustedElapsedTime();
		}

		return rankedTimes;
	}

	@Override
	public int[] getRidersPointsInStage(int stageId) throws IDNotRecognisedException {
		// Check if the stageId exists in the system
		Stage stage = stages.get(stageId);
		if (stage == null) {
			throw new IDNotRecognisedException("Stage ID not recognised.");
		}

		// Get the results for the stage
		List<Result> results = stage.getResults().values().stream().collect(Collectors.toList());

		// Sort the results by elapsed time using Comparator
		results.sort(Comparator.comparingLong(Result::getElapsedSeconds));

		// Calculate points based on position
		int[] points = new int[results.size()];
		for (int i = 0; i < results.size(); i++) {
			points[i] = calculatePoints(i + 1);
		}

		return points;
	}

	@Override
	public int[] getRidersMountainPointsInStage(int stageId) throws IDNotRecognisedException {
		// Check if the stageId exists in the system
		Stage stage = stages.get(stageId);
		if (stage == null) {
			throw new IDNotRecognisedException("Stage ID not recognised.");
		}

		// Get the riders rank in the stage
		int[] riderIds = getRidersRankInStage(stageId);

		// Calculate mountain points for each rider based on their finish time
		List<Integer> mountainPointsList = new ArrayList<>();
		for (int riderId : riderIds) {
			Rider rider = riders.get(riderId);
			if (rider != null && stage.getType() == StageType.HIGH_MOUNTAIN) {
				int mountainPoints = calculateMountainPoints(rider, stage);
				mountainPointsList.add(mountainPoints);
			}
		}

		// Convert List<Integer> to int[]
		int[] mountainPointsArray = new int[mountainPointsList.size()];
		for (int i = 0; i < mountainPointsList.size(); i++) {
			mountainPointsArray[i] = mountainPointsList.get(i);
		}

		return mountainPointsArray;

	}

	@Override
	public void eraseCyclingPortal() {
		races.clear();
		nextRaceId.set(1);
		teams.clear();
		nextTeamId.set(1);
		stages.clear();
		nextStageId.set(1);
		nextCheckpointId.set(1);
		riders.clear();
		nextRiderId.set(1);

	}

	@Override
	public void saveCyclingPortal(String filename) throws IOException {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
			oos.writeObject(races);
			oos.writeObject(teams);
			oos.writeObject(stages);
			oos.writeObject(riders);
			oos.writeObject(nextRaceId);
			oos.writeObject(nextTeamId);
			oos.writeObject(nextStageId);
			oos.writeObject(nextCheckpointId);
			oos.writeObject(nextRiderId);
		} catch (IOException e) {
			throw new IOException("Error saving MiniCyclingPortal: " + e.getMessage());
		}

	}

	@Override
	public void loadCyclingPortal(String filename) throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
			races = (Map<Integer, Race>) ois.readObject();
			teams = (Map<Integer, Team>) ois.readObject();
			stages = (Map<Integer, Stage>) ois.readObject();
			riders = (Map<Integer, Rider>) ois.readObject();
			nextRaceId = (AtomicInteger) ois.readObject();
			nextTeamId = (AtomicInteger) ois.readObject();
			nextStageId = (AtomicInteger) ois.readObject();
			nextCheckpointId = (AtomicInteger) ois.readObject();
			nextRiderId = (AtomicInteger) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new IOException("Error loading MiniCyclingPortal: " + e.getMessage());
		}

	}

	@Override
	public void removeRaceByName(String name) throws NameNotRecognisedException {
		boolean raceFound = false;
		Iterator<Map.Entry<Integer, Race>> iterator = races.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, Race> entry = iterator.next();
			Race race = entry.getValue();
			if (race.getName().equals(name)) {
				iterator.remove(); // Remove the race entry from the map
				raceFound = true;
				break;
			}
		}
		if (!raceFound) {
			throw new NameNotRecognisedException("Race with name '" + name + "' not found.");
		}

	}

	@Override
	public LocalTime[] getGeneralClassificationTimesInRace(int raceId) throws IDNotRecognisedException {
		// Check if the raceId exists in the system
		if (!races.containsKey(raceId)) {
			throw new IDNotRecognisedException("Race ID not recognised.");
		}

		Race race = races.get(raceId);

		// Get all stages associated with the race
		List<Stage> stages = race.getStages();

		// Create a map to store the total adjusted elapsed time for each rider
		Map<Integer, Long> totalAdjustedTimesMap = new HashMap<>();

		// Iterate through each stage and calculate total adjusted elapsed time for each
		// rider
		for (Stage stage : stages) {
			Map<Integer, Result> results = stage.getResults();
			for (Result result : results.values()) {
				int riderId = result.getRiderId();
				LocalTime elapsedTime = result.getElapsedTime();

				// Calculate total seconds from LocalTime
				long totalSeconds = elapsedTime.toSecondOfDay();

				// Add the total seconds to the total for the rider
				totalAdjustedTimesMap.merge(riderId, totalSeconds, Long::sum);
			}
		}

		// Create a list of Map.Entry objects for sorting by value (total adjusted time)
		List<Map.Entry<Integer, Long>> sortedRidersList = new ArrayList<>(totalAdjustedTimesMap.entrySet());
		sortedRidersList.sort(Map.Entry.comparingByValue());

		// Extract and return the sorted array of rider times as LocalTime objects
		LocalTime[] generalClassificationTimes = sortedRidersList.stream()
				.map(entry -> LocalTime.ofSecondOfDay(entry.getValue())).toArray(LocalTime[]::new);

		return generalClassificationTimes;
	}

	@Override
	public int[] getRidersPointsInRace(int raceId) throws IDNotRecognisedException {
		// Check if the raceId exists in the system
		if (!races.containsKey(raceId)) {
			throw new IDNotRecognisedException("Race ID not recognised.");
		}

		// Get the race from the map
		Race race = races.get(raceId);

		List<Integer> pointsList = new ArrayList<>();

		// Iterate through riders in the race and calculate points for each rider
		for (Rider rider : riders.values()) {
			int riderPoints = calculateRiderPointsInRace(rider, race);
			pointsList.add(riderPoints);
		}

		// Convert the list to an array
		int[] pointsArray = pointsList.stream().mapToInt(Integer::intValue).toArray();

		return pointsArray;
	}

	@Override
	public int[] getRidersMountainPointsInRace(int raceId) throws IDNotRecognisedException {
		// Check if the raceId exists in the system
		if (!races.containsKey(raceId)) {
			throw new IDNotRecognisedException("Race ID not recognised.");
		}

		// Get the race from the map
		Race race = races.get(raceId);

		List<Integer> mountainPointsList = new ArrayList<>();

		// Iterate through stages in the race and calculate mountain points for each
		// rider in each stage
		for (Stage stage : race.getStages()) {
			for (Rider rider : riders.values()) {
				try {
					int mountainPoints = calculateMountainPoints(rider, stage);
					mountainPointsList.add(mountainPoints);
				} catch (IllegalArgumentException e) {
					// Rider not found in the specified stage, continue to the next rider
				}
			}
		}
		// Convert the list to an array
		int[] mountainPointsArray = mountainPointsList.stream().mapToInt(Integer::intValue).toArray();

		return mountainPointsArray;

	}

	@Override
	public int[] getRidersGeneralClassificationRank(int raceId) throws IDNotRecognisedException {

		// Check if the raceId exists in the system
		if (!races.containsKey(raceId)) {
			throw new IDNotRecognisedException("Race ID not recognised.");
		}

		Race race = races.get(raceId);

		// Get all stages associated with the race
		List<Stage> stages = race.getStages();

		// Create a map to store the total adjusted elapsed time for each rider
		Map<Integer, Long> totalAdjustedTimesMap = new HashMap<>();

		// Iterate through each stage and calculate total adjusted elapsed time for each
		// rider
		for (Stage stage : stages) {
			Map<Integer, Result> results = stage.getResults();
			for (Result result : results.values()) {
				int riderId = result.getRiderId();
				LocalTime elapsedTime = result.getElapsedTime();

				// Calculate total seconds from LocalTime
				long totalSeconds = elapsedTime.toSecondOfDay();

				// Add the total seconds to the total for the rider
				totalAdjustedTimesMap.merge(riderId, totalSeconds, Long::sum);
			}
		}

		// Create a list of Map.Entry objects for sorting by value (total adjusted time)
		List<Map.Entry<Integer, Long>> sortedRidersList = new ArrayList<>(totalAdjustedTimesMap.entrySet());
		sortedRidersList.sort(Map.Entry.comparingByValue());

		// Extract and return the sorted array of rider IDs
		int[] rankedRiders = sortedRidersList.stream().mapToInt(Map.Entry::getKey).toArray();
		return rankedRiders;

	}

	@Override
	public int[] getRidersPointClassificationRank(int raceId) throws IDNotRecognisedException {
		if (!races.containsKey(raceId)) {
			throw new IDNotRecognisedException("Race ID not recognised.");
		}

		Race race = races.get(raceId);
		List<Rider> riders = getRidersInRace(race); // Assuming you have a method to get riders in a race
		List<RiderPoints> riderPointsList = new ArrayList<>();

		// Calculate points for each rider in the race across all stages
		for (Rider rider : riders) {
			int totalPoints = 0;
			for (Stage stage : race.getStages()) {
				if (stage.getResults().containsKey(rider.getId())) {
					int stagePoints = calculateMountainPoints(rider, stage);
					totalPoints += stagePoints;
				}
			}
			riderPointsList.add(new RiderPoints(rider.getId(), totalPoints));
		}
		// Sort the riders based on their total points
		riderPointsList.sort(Comparator.comparingInt(RiderPoints::getTotalPoints).reversed());

		// Extract rider IDs from the sorted list
		int[] rankedRiders = riderPointsList.stream().mapToInt(RiderPoints::getRiderId).toArray();

		return rankedRiders;
	}

	@Override
	public int[] getRidersMountainPointClassificationRank(int raceId) throws IDNotRecognisedException {
		if (!races.containsKey(raceId)) {
			throw new IDNotRecognisedException("Race ID not recognised.");
		}

		Race race = races.get(raceId);
		List<Rider> riders = getRidersInRace(race); // Assuming you have a method to get riders in a race
		List<RiderPoints> riderPointsList = new ArrayList<>();

		// Calculate points for each rider in the race across all stages
		for (Rider rider : riders) {
			int totalPoints = 0;
			for (Stage stage : race.getStages()) {
				if (stage.getResults().containsKey(rider.getId())&& stage.getType()==StageType.HIGH_MOUNTAIN) {
					int stagePoints = calculateMountainPoints(rider, stage);
					totalPoints += stagePoints;
				}
			}
			riderPointsList.add(new RiderPoints(rider.getId(), totalPoints));
		}
		// Sort the riders based on their total points
		riderPointsList.sort(Comparator.comparingInt(RiderPoints::getTotalPoints).reversed());

		// Extract rider IDs from the sorted list
		int[] rankedRiders = riderPointsList.stream().mapToInt(RiderPoints::getRiderId).toArray();

		return rankedRiders;
	}

	private boolean isAdjustedTimeNeeded(Result result, int riderId) {
		// Example logic to determine if adjusted time is needed, adjust as per your
		// requirements
		return result.getGapWithPreviousRider(riderId) < 1; // Adjust if gap is less than 1 second
	}

	private LocalTime adjustElapsedTime(LocalTime elapsedTime, Stage stage, Result result) {
		// Example adjustment logic, adjust as per your requirements
		return elapsedTime.minusSeconds(1); // Adjust by subtracting 1 second
	}

	private int calculatePoints(int position) {
		// Implement your points calculation logic here
		// Example: 1st place gets 100 points, 2nd place gets 90 points, and so on
		return 100 - (position - 1) * 10;
	}

	private int calculateMountainPoints(Rider rider, Stage stage) {
		// Check if the stage is a high mountain stage
		if (stage.getType() != StageType.HIGH_MOUNTAIN) {
			throw new IllegalArgumentException("The stage is not a high mountain stage.");
		}

		// Get the rider's finishing position in the stage
		int position = getRiderPositionInStage(rider, stage);

		// Assign mountain points based on finishing position
		int mountainPoints;
		if (position == 1) {
			mountainPoints = 20; // Winner gets 20 points
		} else if (position <= 5) {
			mountainPoints = 15; // Top 5 riders get 15 points
		} else if (position <= 10) {
			mountainPoints = 10; // Riders finishing between 6th and 10th get 10 points
		} else {
			mountainPoints = 5; // Riders finishing after 10th position get 5 points
		}

		return mountainPoints;
	}

	private int getRiderPositionInStage(Rider rider, Stage stage) {
		// Get the results for the specified stage
		List<Result> results = stage.getResults().values().stream()
				.filter(result -> result.getRiderId() == rider.getId()).collect(Collectors.toList());

		if (results.isEmpty()) {
			throw new IllegalArgumentException("No results found for the rider in the specified stage.");
		}

		// Sort the results by elapsed time using Comparator
		results.sort(Comparator.comparingLong(Result::getElapsedSeconds));

		// Find the position of the rider in the sorted results
		int position = 1;
		for (Result result : results) {
			if (result.getRiderId() == rider.getId()) {
				break; // Found the rider, exit loop
			}
			position++;
		}

		return position;
	}

	// Helper function to calculate points for a rider in a race
	private int calculateRiderPointsInRace(Rider rider, Race race) {
		int totalPoints = 0;

		// Iterate through stages in the race and calculate points for the rider in each
		// stage
		for (Stage stage : race.getStages()) {
			try {
				int stagePoints = calculateMountainPoints(rider, stage);
				totalPoints += stagePoints;
			} catch (IllegalArgumentException e) {
				// Rider not found in the specified stage, continue to the next stage
			}
		}

		return totalPoints;
	}

	// Helper method to get riders in a race
	private List<Rider> getRidersInRace(Race race) {
		List<Rider> riders = new ArrayList<>();
		for (Stage stage : race.getStages()) {
			riders.addAll(stage.getResults().values().stream().map(result -> riders.get(result.getRiderId()))
					.collect(Collectors.toList()));
		}
		return riders;
	}

	


}
