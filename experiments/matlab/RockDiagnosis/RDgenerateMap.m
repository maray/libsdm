function RDgenerateMap(rocks,siz)

file=['RDmap-r' num2str(rocks) '-s' num2str(siz) '.mat'];
xsize=siz;
ysize=siz;
rock_pos=zeros(1,1);
while size(rock_pos,1)<rocks
    rock_pos=unique(randi(xsize*ysize,rocks,1)-1);
end
rock_pos;
y_pos=int16(floor(rock_pos/(xsize)));
x_pos=int16(mod(rock_pos,(xsize)));

graphical=zeros(siz,siz);
for i=1:rocks
    graphical(x_pos(i)+1,y_pos(i)+1)=40;
end
graphical
image(graphical);

save(file,'xsize','ysize','y_pos','x_pos','rocks','graphical');

end

