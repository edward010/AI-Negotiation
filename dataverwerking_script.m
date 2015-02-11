
%% result cell array:
%name1% %average1% %StandardDeviation1% %name2% %average2% %StandardDeviation2%...

clear all
listing = dir('Log files');
for i=3:size(listing)
    try
        try
            file=importfile(listing(i).name);
            count=0;
            while count<size(file,1)-1
                group1(count+1)=file{2+count,15};
                group1(count+2)=file{3+count,15};
                group1(count+3)=file{4+count,16};
                group1(count+4)=file{5+count,17};
                group1(count+5)=file{6+count,17};
                group1(count+6)=file{7+count,16};
                count=count+6;
            end
            point=strfind(file{2,12},'.');
            groupname1=file{2,12}([point(2)+1:strfind(file{2,12},'@')-1]);
            
            count=0;
            while count<size(file,1)-1
                group2(count+1)=file{2+count,16};
                group2(count+2)=file{3+count,17};
                group2(count+3)=file{4+count,17};
                group2(count+4)=file{5+count,16};
                group2(count+5)=file{6+count,15};
                group2(count+6)=file{7+count,15};
                count=count+6;
            end
            point=strfind(file{2,13},'.');
            groupname2=file{2,13}([point(2)+1:strfind(file{2,13},'@')-1]);
            
            count=0;
            while count<size(file,1)-1
                group3(count+1)=file{2+count,17};
                group3(count+2)=file{3+count,16};
                group3(count+3)=file{4+count,15};
                group3(count+4)=file{5+count,15};
                group3(count+5)=file{6+count,16};
                group3(count+6)=file{7+count,17};
                count=count+6;
            end
            point=strfind(file{2,14},'.');
            groupname3=file{2,14}([point(2)+1:strfind(file{2,14},'@')-1]);
            
        catch e
            clear file
            file=importfile1(listing(i).name);
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
        end
        
        
        a=strfind(listing(i).name,'a');
        b=strfind(listing(i).name,'b');
        c=strfind(listing(i).name,'c');
        
        if ~isnan(a)
            results_easyt{i-2,1} = groupname1;
            results_easyt{i-2,2} = mean(group1);
            results_easyt{i-2,3} = std(group1);
            results_easyt{i-2,4} = groupname2;
            results_easyt{i-2,5} = mean(group2);
            results_easyt{i-2,6} = std(group2);
            results_easyt{i-2,7} = groupname3;
            results_easyt{i-2,8} = mean(group3);
            results_easyt{i-2,9} = std(group3);
        elseif ~isnan(b)
            results_mediumt{i-2,1} = groupname1;
            results_mediumt{i-2,2} = mean(group1);
            results_mediumt{i-2,3} = std(group1);
            results_mediumt{i-2,4} = groupname2;
            results_mediumt{i-2,5} = mean(group2);
            results_mediumt{i-2,6} = std(group2);
            results_mediumt{i-2,7} = groupname3;
            results_mediumt{i-2,8} = mean(group3);
            results_mediumt{i-2,9} = std(group3);
        elseif ~isnan(c)
            results_hardt{i-2,1} = groupname1;
            results_hardt{i-2,2} = mean(group1);
            results_hardt{i-2,3} = std(group1);
            results_hardt{i-2,4} = groupname2;
            results_hardt{i-2,5} = mean(group2);
            results_hardt{i-2,6} = std(group2);
            results_hardt{i-2,7} = groupname3;
            results_hardt{i-2,8} = mean(group3);
            results_hardt{i-2,9} = std(group3);
        end
        
        clear group1 group2 group3
    catch e
    end
end
results_easy=results_easyt((~cellfun('isempty',results_easyt)));
results_easy=reshape(results_easy,size(results_easy,1)/9,9);
results_medium=results_mediumt((~cellfun('isempty',results_mediumt)));
results_medium=reshape(results_medium,size(results_medium,1)/9,9);
results_hard=results_hardt((~cellfun('isempty',results_hardt)));
results_hard=reshape(results_hard,size(results_hard,1)/9,9);

clear results_easyt results_mediumt results_hardt

save('results_easy.mat','results_easy')
save('results_medium.mat','results_medium')
save('results_hard.mat','results_hard')