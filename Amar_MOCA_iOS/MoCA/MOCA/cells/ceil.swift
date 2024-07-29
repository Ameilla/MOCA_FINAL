//
//  ceil.swift
//  MOCA
//
//  Created by SAIL on 12/10/23.
//

import UIKit

class ceil: UITableViewCell {

    @IBOutlet weak var lbl: UILabel!
    
    @IBOutlet weak var box: UIView!
    @IBOutlet weak var patientImg : UIImageView!
    @IBOutlet weak var age: UILabel!
    @IBOutlet weak var diagnosis: UILabel!
    override func awakeFromNib() {
        super.awakeFromNib()
        
        patientImg.contentMode = .scaleAspectFit
              patientImg.clipsToBounds = true
              patientImg.translatesAutoresizingMaskIntoConstraints = false
//        
//        let separator = UIView(frame: CGRect(x: 0, y: self.frame.height - 10, width: self.frame.width, height: 10)) // Adjust gap size
//                separator.backgroundColor = .clear // or any color you want
//                self.contentView.addSubview(separator)
          }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        
    }
    override func layoutSubviews() {
        super.layoutSubviews()
        
        let margin = UIEdgeInsets(top: 5, left: 5, bottom: 5,right: 5)
        contentView.frame = contentView.frame.inset(by: margin)
        contentView.layer.cornerRadius = 10
    }

      }
