clear all;
clear java;
libsdm

pomdp=FileParser.loadPomdp('tiger/tiger.95.POMDP',FileParser.PARSE_CASSANDRA_POMDP);
projector=SparsePomdpProjector(pomdp);
PazTotal=zeros(pomdp.states,pomdp.states);
for a=1:pomdp.actions
    for z=1:pomdp.observations
        Paz{a}{z}=projector.getTau(a-1,z-1,0).getArray();
        PazTotal=PazTotal + Paz{a}{z};
    end
end
%b=rand(92,1);
%b=b./sum(b);
%B(:,1)=b;
%for i=2:50
%    a=randi(pomdp.actions,1)
%    z=randi(pomdp.observations-1,1)
%    b=Paz{a}{z}*b
%    b=b./sum(b)
%    B(:,i)=b;
%end
    