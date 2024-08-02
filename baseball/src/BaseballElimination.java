import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FlowEdge;

import java.util.HashMap;
import java.util.Map;

public class BaseballElimination {

    private final int numTeams;
    private final Map<String, int[]> teamInfo;
    // I think this second map will be useful, though the info
    // is already saved in teamInfo
    private final Map<Integer, String> indexToTeamName;
    private final int[][] matchupArray;
    private final Map<String, Stack<String>> eliminationSubsets;


    public BaseballElimination(String filename)                    // create a baseball division from given filename in format specified below
    {
        // first just load all the relevant info
        In input = new In(filename);
        if (!input.exists()) {
            throw new RuntimeException("Failed to open filename: " + filename);
        }
        numTeams = input.readInt();

        // eliminationSubsets = new String[numTeams][]; // don't know the other length
        teamInfo = new HashMap<>(numTeams);
        indexToTeamName = new HashMap<>(numTeams);
        eliminationSubsets = new HashMap<>(numTeams);
        matchupArray = new int[numTeams][numTeams];


        // line is: team #wins #losses #remaining and then n matchups
        for (int line = 0; line < numTeams; line++) {
            String teamName = input.readString();
            // this is pretty inefficient,
            // but it shows what is happening
            int numWins = input.readInt();
            int numLosses = input.readInt();
            int remaining = input.readInt();
            // also need to add the line so
            // we can get the index it has for the matchup array
            teamInfo.put(teamName, new int[]{numWins, numLosses, remaining, line});
            indexToTeamName.put(line, teamName);

            // now load the matchup data
            for (int i = 0; i < numTeams; i++) {
                int val = input.readInt();
                matchupArray[line][i] = val;
            }
        }

        // so now all data is loaded... time to eliminate
        trivialElimination();

        // now, for every team that wasn't eliminated trivially, attempt a max flow elimination
        for (String team : teamInfo.keySet()) {
            if (!eliminationSubsets.containsKey(team)) {
                maxFlowElimination(team);
            }
        }


    }

    private void trivialElimination() {
        for (String team1 : teamInfo.keySet()) {
            int team1Wins = teamInfo.get(team1)[0];
            int team1Remaining = teamInfo.get(team1)[2];
            for (String team2 : teamInfo.keySet()) {
                if (!team1.equals(team2)) {
                    int team2Wins = teamInfo.get(team2)[0];
                    if (team1Wins + team1Remaining < team2Wins) {
                        // team1 is mathematically eliminated
                        // put new entry into elimination subsets
                        // push team2 onto corresponding stack
                        // probably better way to do this?
                        eliminationSubsets.put(team1, new Stack<>());
                        eliminationSubsets.get(team1).push(team2);
                    }
                }
            }
        }
    }

    private void maxFlowElimination(String team) {
        // note that this does it for a SINGLE team... as opposed to the previous...
        FlowNetwork network;
        int[] info = teamInfo.get(team);
        int teamIndex = info[3];
        int maxPotentialWins = info[0] + info[2];
        int numMatchups = numTeams * (numTeams - 1) / 2;
        int numVertices = 2 + numMatchups + (numTeams - 1);

        // I am going to make the ENTIRE network,
        // and then just add the relevant edges
        network = new FlowNetwork(numVertices);
        // let's say numVertices - 2 is the source, numVertices - 1 is the sink
        int sourceIndex = numVertices - 2;
        int sinkIndex = numVertices - 1;

        // first add in the edges for the teams to the sink
        for (int i = 0; i < numTeams; i++) {
            // this is a bit awkward, but I need hashmaps for my get methods
            String otherTeam = indexToTeamName.get(i);
            int otherWins = teamInfo.get(otherTeam)[0];
            if (i == teamIndex) {
                continue;
            }
            int weight = maxPotentialWins - otherWins;
            FlowEdge edge = new FlowEdge(i, sinkIndex, weight);
            network.addEdge(edge);
        }

        // now, add edges for the matchups
        // have to switch to count for indexing the matchups
        int count = numTeams;
        // only pick out elements starting at the diagonals
        int totalRemainingMatches = 0;
        // StdOut.printf("source index: %d\n", sourceIndex);
        // StdOut.printf("sink index: %d\n", sinkIndex);
        // StdOut.printf("excluded index: %d\n", teamIndex);
        for (int i = 0; i < numTeams; i++) {
            // skip row if it is the matchups for the selected team
            for (int j = i; j < numTeams; j++) {
                if ((j != i) && (i != teamIndex) && (j != teamIndex)) {
                    // StdOut.printf("Matrix i,j = %d,%d, count = %d\n", i, j, count);
                    // int matchIndex = numTeams * i + j;
                    int matchCount = matchupArray[i][j];
                    // need these edges (i dont think there is an integer infinity?)
                    FlowEdge sourceToMatchup = new FlowEdge(sourceIndex, count, matchCount);
                    FlowEdge toTeam1 = new FlowEdge(count, i, Double.POSITIVE_INFINITY);
                    FlowEdge toTeam2 = new FlowEdge(count, j, Double.POSITIVE_INFINITY);
                    // add to network
                    network.addEdge(sourceToMatchup);
                    network.addEdge(toTeam1);
                    network.addEdge(toTeam2);
                    totalRemainingMatches += matchCount;
                    count++;
                    // StdOut.printf("count: %d\n", count);
                }

            }
        }

        // Ok, now do Ford Fulkerson algorithm
        FordFulkerson pathFinder = new FordFulkerson(network, sourceIndex, sinkIndex);
        // Ok so if the team is eliminated...
        // all the edges that point FROM S are full...
        // this means that the max flow is equal to the total number of matches
        if (pathFinder.value() < totalRemainingMatches) {
            // if in here, the team gets eliminated
            // build the set that eliminates the team...
            Stack<String> eliminationSet = new Stack<>();
            for (int i = 0; i < numTeams; i++) {
                if (pathFinder.inCut(i)) {
                    eliminationSet.push(indexToTeamName.get(i));
                }
            }
            eliminationSubsets.put(team, eliminationSet);
        }

        // Ok, that should be it


    }


    public int numberOfTeams()                        // number of teams
    {
        return this.numTeams;
    }

    public Iterable<String> teams()                                // all teams
    {
        return teamInfo.keySet();
    }

    public int wins(String team)                      // number of wins for given team
    {
        if (!teamInfo.containsKey(team)) {
            throw new IllegalArgumentException("Team is not in the data");
        }

        return teamInfo.get(team)[0];
    }


    public int losses(String team)                    // number of losses for given team
    {
        if (!teamInfo.containsKey(team)) {
            throw new IllegalArgumentException("Team is not in the data");
        }

        return teamInfo.get(team)[1];


    }

    public int remaining(String team)                 // number of remaining games for given team
    {
        if (!teamInfo.containsKey(team)) {
            throw new IllegalArgumentException("Team is not in the data");
        }

        return teamInfo.get(team)[2];
    }

    public int against(String team1, String team2)    // number of remaining games between team1 and team2
    {
        if (!teamInfo.containsKey(team1)) {
            throw new IllegalArgumentException("Team1 is not in the data");
        }
        if (!teamInfo.containsKey(team2)) {
            throw new IllegalArgumentException("Team2 is not in the data");
        }

        int idx1 = teamInfo.get(team1)[3];
        int idx2 = teamInfo.get(team2)[3];
        return matchupArray[idx1][idx2];


    }


    public boolean isEliminated(String team)              // is given team eliminated?
    {
        if (!teamInfo.containsKey(team)) {
            throw new IllegalArgumentException("Team is not in the data");
        }

        return eliminationSubsets.containsKey(team);

    }

    public Iterable<String> certificateOfElimination(String team)  // subset R of teams that eliminates given team; null if not eliminated
    {
        if (!teamInfo.containsKey(team)) {
            throw new IllegalArgumentException("Team is not in the data");
        }

        return eliminationSubsets.get(team);

    }


    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}