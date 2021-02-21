insert into user_entity (
                        id,
                        email,
                        password,
                        first_name,
                        last_name,
                        city,
                        country)
       values (
              '7fd3dcbf-6a5a-4e01-980c-58731a101d3e',
              'writer1@gmail.com',
              '$2y$12$1yQslQXHtYQOfYMxePUQn.Uig1Dwb1UcKoz8ms9lwf9pcL.setoCe',
              'Writer1',
              'Writer1',
              'Novi Sad',
              'Serbia'), --password: user123
('185c0af9-a32d-4df5-8429-edbe72dffa5f',
'writer2@gmail.com',
'$2y$12$MLWCUxUMq5cWXHko1sUNteZN5QzT54Yp/rmCTvsLKY9xfTSGuOPGa',
'Writer2',
'Writer2',
'Belgrade',
'Serbia'), --password: user123
('f77721aa-9c77-4587-9853-c187358e47a8',
'writer3@gmail.com',
'$2y$12$XQjs87oIICnzjR9mXut4l.gaRF3Xvqxw3QsdTeeS.syH2JH4egpuG',
'Writer3',
'Writer3',
'Rome',
'Italy'), --password: user123
('6e5eed16-eca7-427c-a3e3-987b2443a26c',
'reviewer1@gmail.com',
'$2y$12$WeLMT8ZS6owrHg5/s617deU9nhsQlZZ1eqau66F.Ok4qA59mrRwha',
'Rewiewer1',
'Rewiewer1',
'Belgrade',
'Serbia'), --password: user123
('008f675c-5d59-4edf-83f8-6c1fd756dda5',
'reviewer2@gmail.com',
'$2y$12$49636cv9c9E.r9xwyVWYouON9P.WeSJBKzOlIhqgwD8BMJwKwnyK6',
'Reviewer2',
'Reviewer2',
'Rome',
'Italy') ; --password: user123

insert into writer (
                   id,
                   user_id)
       values (
              '790e1684-f098-4bc4-b1c8-080a46a19feb',
              '7fd3dcbf-6a5a-4e01-980c-58731a101d3e'),
('2ba2af07-bd52-40d0-aebc-7431d4397ed7',
'185c0af9-a32d-4df5-8429-edbe72dffa5f'),
('60918e31-dfe1-4ca2-a1f4-be950f6c7457',
'f77721aa-9c77-4587-9853-c187358e47a8');

insert into genre (
                  id,
                  name)
       values (
              '07a9ec90-12a8-4111-b117-4776f82da0ac',
              'ACTION'),
('89484792-7f4f-4c8b-bc50-a093266bcceb', 'CRIME'),
('05869463-6363-4c10-a5b5-02e52f6f099e', 'ROMANCE') ;

insert into user_genre (
                       user_id,
                       genre_id)
       values (
              '7fd3dcbf-6a5a-4e01-980c-58731a101d3e',
              '07a9ec90-12a8-4111-b117-4776f82da0ac'),
('185c0af9-a32d-4df5-8429-edbe72dffa5f', '89484792-7f4f-4c8b-bc50-a093266bcceb'),
('f77721aa-9c77-4587-9853-c187358e47a8', '05869463-6363-4c10-a5b5-02e52f6f099e'),
('6e5eed16-eca7-427c-a3e3-987b2443a26c', '05869463-6363-4c10-a5b5-02e52f6f099e'),
('008f675c-5d59-4edf-83f8-6c1fd756dda5', '05869463-6363-4c10-a5b5-02e52f6f099e') ;

insert into reviewer (
                     id,
                     user_id)
       values (
              'b789da88-fc60-497b-9aed-ab93219a3ec4',
              '6e5eed16-eca7-427c-a3e3-987b2443a26c'),
('203f5218-ea2d-4b12-ba98-9996b76be18b',
'008f675c-5d59-4edf-83f8-6c1fd756dda5') ;
