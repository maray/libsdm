function newres = accuRewards(res,gamma)
    [trials,horizon]=size(res);
    newres=zeros(trials,horizon);
    accu=zeros(trials,1);
    for i=1:horizon
        accu=accu+gamma^(i-1)*res(:,i);
        newres(:,i)=accu;
    end
end

