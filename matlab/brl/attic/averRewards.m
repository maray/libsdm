function newres = averRewards(res)
    [trials,horizon]=size(res);
    newres=zeros(trials,horizon);
    accu=zeros(trials,1);
    for i=1:horizon
        accu=accu+res(:,i);
        newres(:,i)=accu/i;
    end
end

