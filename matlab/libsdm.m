lsdm_path=mfilename('fullpath');
lsdm_path=lsdm_path(1:end-14);

javaaddpath '/usr/share/java/antlr3-runtime.jar'
javaaddpath '/usr/share/java/glpk-java.jar'

java.lang.System.gc()

%jpath=strcat(lsdm_path,'/external/concurrent.jar');
%javaaddpath(jpath)
jpath=strcat(lsdm_path,'/external/matlabcontrol-4.1.0.jar');
javaaddpath(jpath)
jpath=strcat(lsdm_path,'/dist/libsdm.jar');
javaaddpath(jpath)



namespace;

%LpSolver.setProgram(LpSolver.PROG_MATLAB);

%addpath strcat(lsdm_path,'/matlab/pomdp');
