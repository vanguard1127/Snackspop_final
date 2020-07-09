//
//  NormalNavView.m
//  snackspopnew
//
//  Created by Admin on 2019-04-08.
//  Copyright © 2019 TestDemo. All rights reserved.
//

#import "NormalNavView.h"
@interface NormalNavView() <UITableViewDataSource, UITableViewDelegate>

@end
@implementation NormalNavView


- (void) awakeFromNib
{
    [super awakeFromNib];
    
    tableData = [NSArray arrayWithObjects:@"Home",  @"About us", @"Log-in", nil];
    
    thumbnails = [NSArray arrayWithObjects:@"ic_nav_home" , @"ic_help" , @"ic_menu_logout",nil];
    
    self.mLblName.text = @"";
    [self.tableView registerNib:[UINib nibWithNibName:@"raw_nav_table_cell" bundle:nil]
         forCellReuseIdentifier:@"NavTableViewCellId"];
}
- (void)setUserName:(NSString *)name{
    self.mLblName.text = name;
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
    return 3;
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
    UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
    if (self.menuCallback) {
        self.menuCallback(indexPath.row, cell.textLabel.text);
    }
}

@end
