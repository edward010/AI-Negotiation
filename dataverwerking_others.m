listing = dir('Excel files\Others');
for i=3:size(listing);
file=importfile_others(listing(i).name);
count=0;
while count<size(file,1)-2
    group1(count+1)=file{3+count,15};
    group1(count+2)=file{4+count,15};
    group1(count+3)=file{5+count,16};
    group1(count+4)=file{6+count,17};
    group1(count+5)=file{7+count,17};
    group1(count+6)=file{8+count,16};
    count=count+6;
end
point=strfind(file{3,12},'.');
groupname1=file{3,12}([point(2)+1:strfind(file{3,12},'@')-1]);


count=0;
while count<size(file,1)-2
    group2(count+1)=file{3+count,16};
    group2(count+2)=file{4+count,17};
    group2(count+3)=file{5+count,17};
    group2(count+4)=file{6+count,16};
    group2(count+5)=file{7+count,15};
    group2(count+6)=file{8+count,15};
    count=count+6;
end
point=strfind(file{3,13},'.');
groupname2=file{3,13}([point(2)+1:strfind(file{3,13},'@')-1]);

count=0;
while count<size(file,1)-2
    group3(count+1)=file{3+count,17};
    group3(count+2)=file{4+count,16};
    group3(count+3)=file{5+count,15};
    group3(count+4)=file{6+count,15};
    group3(count+5)=file{7+count,16};
    group3(count+6)=file{8+count,17};
    count=count+6;
end
point=strfind(file{3,14},'.');
groupname3=file{3,14}([point(2)+1:strfind(file{3,14},'@')-1]);
group1=group1/100000;
group2=group2/100000;
group3=group3/100000;

results{i-2,1} = groupname1;
results{i-2,2} = mean(group1);
results{i-2,3} = std(group1);
results{i-2,4} = groupname2;
results{i-2,5} = mean(group2);
results{i-2,6} = std(group2);
results{i-2,7} = groupname3;
results{i-2,8} = mean(group3);
results{i-2,9} = std(group3);



end

results=results((~cellfun('isempty',results)));
results=reshape(results,size(results,1)/9,9);

save('results_others.mat','results')