//
//  LoginNavView.m
//  snackspopnew
//
//  Created by Admin on 2019-04-06.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "LoginNavView.h"
#import "KafkaRefresh.h"

@interface LoginNavView() <UITableViewDataSource, UITableViewDelegate>

@end
@implementation LoginNavView
NSArray *tableData;
NSArray *thumbnails;

- (void) awakeFromNib
{
    [super awakeFromNib];
    
    tableData = [NSArray arrayWithObjects:@"Home", @"My Profile", @"My Products", @"My Chats", @"About us", @"Log-out", nil];
    
    thumbnails = [NSArray arrayWithObjects:@"ic_nav_home",@"ic_menu_profile",@"ic_product", @"ic_message" , @"ic_help" , @"ic_menu_logout",nil];

    [self.tableView registerNib:[UINib nibWithNibName:@"raw_nav_table_cell" bundle:nil]
         forCellReuseIdentifier:@"NavTableViewCellId"];
}

- (void)setUserName:(NSString *)name{
    self.nameLabel.text = name;
}

- (void)setUserImage:(NSString *)name{
    [self.logoImageView sd_setImageWithURL:[NSURL URLWithString:name ]
                          placeholderImage:[UIImage imageNamed:@"ic_emptyuser"]];
}
- (NSInteger) numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger) tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 6;
}

- (UITableViewCell*) tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{

    static NSString *simpleTableIdentifier = @"NavTableViewCellId";
    
    NavTableViewCell *cell = (NavTableViewCell *)[tableView dequeueReusableCellWithIdentifier:simpleTableIdentifier];
    if (cell == nil)
    {
        NSArray *nib = [[NSBundle mainBundle] loadNibNamed:@"raw_nav_table_cell" owner:self options:nil];
        cell = [nib objectAtIndex:0];
        
        
    }
    if (indexPath.row == 0)
    {
        cell.backgroundColor = [UIColor lightGrayColor];
    }
    
    cell.mLabelView.text = [tableData objectAtIndex:indexPath.row];
    cell.mImageView.image = [UIImage imageNamed:[thumbnails objectAtIndex:indexPath.row]];
    
    return cell;
 
}

#pragma mark - UITableViewDelegate
- (void) tableView:(UITableView *)tableView didHighlightRowAtIndexPath:(NSIndexPath *)indexPath
{

}

- (void) tableView:(UITableView *)tableView didUnhighlightRowAtIndexPath:(NSIndexPath *)indexPath
{

}

- (void) tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    NavTableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    
    [self setSelectTableViewCell:cell];
    
    if (self.menuCallback) {
        self.menuCallback(indexPath.row, cell.textLabel.text);
    }
}

-(void) setSelectTableViewCell:(NavTableViewCell *)cell {
    NSArray<NavTableViewCell *> *cells = [self.tableView visibleCells];
    for (NavTableViewCell *itemCell in cells)
    {
        itemCell.backgroundColor = [UIColor clearColor];
    }
    cell.backgroundColor = [UIColor lightGrayColor];
}



@end
