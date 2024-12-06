select distinct s2.title, s.title, p.title, t.name from NoteSegments ns inner join Notes n on n.id = ns.noteId 
inner join Topics t on t.id = n.topicId inner join Publications p on p.id = t.publicationId 
inner join Subjects s on s.id = p.subjectId inner join Shelfs s2 on s2.id = s.shelfId 
order by s2.title 