listing = dir('Log files');
for i=3:size(listing)
    file=importfile(listing(i).name);
    group1=zeros(60,1);
    count=1;
    while count<60
        group1(count+0)=file{3+i*6,15};
        group1(count+1)=file{4+i*6,15};
        group1(count+2)=file{5+i*6,16};
        group1(count+3)=file{6+i*6,17};
        group1(count+4)=file{7+i*6,17};
        group1(count+5)=file{8+i*6,16};
    count=count+6;
    end
    group1=(group1/100000);
    point=strfind(file{2,12},'.');
    groupname=file{2,12}([point(2)+1:strfind(file{2,12},'@')-1]);
    results{i-2,1} = groupname;
    results{i-2,2} = mean(group1);
    results{i-2,3} = std(group1);
    
    group2=zeros(60,1);
    count=1;
    while count<60
        group2(count+0)=file{3+i*6,16};
        group2(count+1)=file{4+i*6,17};
        group2(count+2)=file{5+i*6,17};
        group2(count+3)=file{6+i*6,16};
        group2(count+4)=file{7+i*6,15};
        group2(count+5)=file{8+i*6,15};
    count=count+6;
    end
    group2=(group2/100000);
    point=strfind(file{2,13},'.');
    groupname=file{2,13}([point(2)+1:strfind(file{2,13},'@')-1]);
    results{i-2,4} = groupname;
    results{i-2,5} = mean(group2);
    results{i-2,6} = std(group2);
    
    group3=zeros(60,1);
    count=1;
    while count<60
        group3(count+0)=file{3+i*6,17};
        group3(count+1)=file{4+i*6,16};
        group3(count+2)=file{5+i*6,15};
        group3(count+3)=file{6+i*6,15};
        group3(count+4)=file{7+i*6,16};
        group3(count+5)=file{8+i*6,17};
    count=count+6;
    end
    group3=(group3/100000);
    point=strfind(file{2,14},'.');
    groupname=file{2,14}([point(2)+1:strfind(file{2,14},'@')-1]);
    results{i-2,7} = groupname;
    results{i-2,8} = mean(group3);
    results{i-2,9} = std(group3);
    
end