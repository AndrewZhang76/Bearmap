package bearmaps.utils.graph;

import bearmaps.utils.pq.MinHeapPQ;
import edu.princeton.cs.algs4.Stopwatch;

import java.lang.reflect.Array;
import java.util.*;

public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex> {
    private SolverOutcome outcome;
    private List<Vertex> solution;
    private double solutionWeight;
    private double explorationTime;
    private int numStatesExplored = 0;

    public AStarSolver(AStarGraph<Vertex> input, Vertex start, Vertex end, double timeout){
        Stopwatch sw = new Stopwatch();
        MinHeapPQ<Vertex> pq = new MinHeapPQ<>();
        /** h(q, goal) */
        double first_pv = input.estimatedDistanceToGoal(start, end);

        Map<Vertex, Double> distTo = new HashMap<>();// the value is the distance from start to key
        Map<Vertex, Vertex> edgeTo = new HashMap(); // the value is the previous vertex of key
        /** insert the start vertex */
        edgeTo.put(start, null);
        distTo.put(start, 0.0);
        pq.insert(start, first_pv);
        double timeSpent = sw.elapsedTime();

        while(!pq.peek().equals(end)){
            Vertex curr = pq.poll();
            numStatesExplored += 1;
            List<WeightedEdge<Vertex>> currNeighbors = input.neighbors(curr);
            /** relax(e) function */
            for(WeightedEdge<Vertex> e: currNeighbors){
                Vertex p = e.from(); Vertex q = e.to(); double w = e.weight();
                if(!distTo.containsKey(q) || distTo.get(p) + w < distTo.get(q)){
                    /** update edgeTO and distTo */
                    distTo.remove(q);
                    distTo.put(q, distTo.get(p) + w);
                    edgeTo.remove(q);
                    edgeTo.put(q, p);
                    double h_val = input.estimatedDistanceToGoal(q, end);
                    if(pq.contains(q)){
                        pq.changePriority(q, distTo.get(q) + h_val);
                    }else{
                        pq.insert(q, distTo.get(q) + h_val);
                    }
                }
            }
            timeSpent = sw.elapsedTime();
            if(timeSpent > timeout){
                outcome = SolverOutcome.TIMEOUT;
                break;
            }
            if(pq.size() == 0){
                outcome = SolverOutcome.UNSOLVABLE;
                break;
            }
        }
        explorationTime = timeSpent;
        if(pq.size() != 0 && pq.peek().equals(end)){
            outcome = SolverOutcome.SOLVED;
            solutionWeight = distTo.get(end);
            Vertex curr = end;
            LinkedList<Vertex> result = new LinkedList<>();
            while(edgeTo.get(curr) != null){
                result.addFirst(curr);
                curr = edgeTo.get(curr);
            }
            result.addFirst(curr);
            solution = result;
        }

    }


    @Override
    public SolverOutcome outcome() {
        return outcome;
    }

    @Override
    public List<Vertex> solution() {
        if(outcome.equals(SolverOutcome.UNSOLVABLE) || outcome.equals(SolverOutcome.TIMEOUT)){
            return null;
        }
        return solution;
    }

    @Override
    public double solutionWeight() {
        if(outcome.equals(SolverOutcome.UNSOLVABLE) || outcome.equals(SolverOutcome.TIMEOUT)) {
            return 0.0;
        }
        return solutionWeight;
    }

    @Override
    public int numStatesExplored() {
        return numStatesExplored;
    }

    @Override
    public double explorationTime() {
        return explorationTime;
    }
}
