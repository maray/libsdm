function total_reward = simulateProblem(algo,problem,horizon,seed)
    simu=liblearning.rl.simulation.SimRL(problem.states,problem.actions,problem.model,problem.reward,problem.gamma);
    vec=simu.simulate(problem.init_state,algo,horizon,seed);
    total_reward=vec.getArray();
end

