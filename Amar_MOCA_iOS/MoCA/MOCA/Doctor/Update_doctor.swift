//
//  Update_doctor.swift
//  MOCA
//
//  Created by AMAR on 26/10/23.
//
//
//struct DoctorImage {
//    var doctorImage: UIImage?
//}
//import UIKit
//
//class Update_doctor: UIViewController,UIImagePickerControllerDelegate, UINavigationControllerDelegate {
//
//    
//    @IBOutlet weak var profileImage: UIImageView!
//    @IBOutlet weak var nameTF: UITextField!
//    @IBOutlet weak var emailTF: UITextField!
//    @IBOutlet weak var designationTF: UITextField!
//    @IBOutlet weak var passwordTF: UITextField!
//    var id:String?
//    var doctor_profile: DoctorProfileModel?
//    var selectedButtonIndex: Int = 0
//    var name,email,password,designation: String?
//    var selectedImages: [DoctorImage] = []
//    var body = Data()
//    let imagePicker = UIImagePickerController()
//    
//    
//    override func viewDidLoad() {
//        super.viewDidLoad()
//        imagePicker.delegate = self
//        imagePicker.allowsEditing = true
//
//        PostAPI()
//        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(dismissKeyboard))
//        self.view.addGestureRecognizer(tapGesture)
//    }
//
//    @IBAction func back(_ sender: Any) {
////        self.navigationController?.popViewController(animated: true)
//        for controller in self.navigationController!.viewControllers as Array {
//            if controller.isKind(of: Doctor_Profile.self) {
//                self.navigationController!.popToViewController(controller, animated: true)
//                break
//            }
//        }
//    }
//    override func viewWillAppear(_ animated: Bool) {
//        PostAPI()
//    }
//    @objc func dismissKeyboard() {
//        view.endEditing(true)
//    }
//
//    @IBAction func save(_ sender: Any) {
//        GettAPI()
//        let storyBoard: UIStoryboard=UIStoryboard(name: "Main", bundle: nil)
//        let vc = storyBoard.instantiateViewController(withIdentifier: "Doctor_Profile") as! Doctor_Profile
//        self.navigationController?.pushViewController(vc, animated: true)
//    }
//    func presentImagePicker(forIndex index: Int) {
//        selectedButtonIndex = index
//        let alert = UIAlertController(title: "Choose Image", message: nil, preferredStyle: .actionSheet)
//        alert.addAction(UIAlertAction(title: "Camera", style: .default, handler: { _ in
//            self.openCamera()
//        }))
//        alert.addAction(UIAlertAction(title: "Gallery", style: .default, handler: { _ in
//            self.openGallery()
//        }))
//        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
//        present(alert, animated: true, completion: nil)
//    }
//
//    func openCamera() {
//        if UIImagePickerController.isSourceTypeAvailable(.camera) {
//            imagePicker.sourceType = .camera
//            present(imagePicker, animated: true, completion: nil)
//        } else {
//            print("Camera not available")
//        }
//    }
//
//    func openGallery() {
//        imagePicker.sourceType = .photoLibrary
//        present(imagePicker, animated: true, completion: nil)
//    }
//
//    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]) {
//        if let pickedImage = info[UIImagePickerController.InfoKey.editedImage] as? UIImage {
//            updateSelectedImages(with: pickedImage)
//        }
//
//        picker.dismiss(animated: true, completion: nil)
//    }
//
//    
//    
//    func PostAPI() {
//        let apiURL = APIList.DoctorProfileApi
//        print(apiURL)
//
//        // Prepare POST parameters if needed
//        let parameters: [String: String] = [:
//            // Add your POST parameters here if required
//            // "key1": value1,
//            // "key2": value2,
//        ]
//
//        APIHandler().postAPIValues(type: DoctorProfileModel.self, apiUrl: apiURL, method: "POST", formData: parameters) { result in
//            switch result {
//            case .success(let data):
//                self.doctor_profile = data
//                DispatchQueue.main.async { [self] in
//                    self.nameTF.text = self.doctor_profile?.name
//                    self.emailTF.text = self.doctor_profile?.email
//                    self.passwordTF.text = self.doctor_profile?.password
//                    self.designationTF.text = self.doctor_profile?.designation
//                    self.setButtonImage(self.profile_img, withImageData: (self.doctor_profile?.image)!)
//                }
//            case .failure(let error):
//                print(error)
//                DispatchQueue.main.async {
//                    let alert = UIAlertController(title: "Warning", message: "Something Went Wrong", preferredStyle: .alert)
//                    alert.addAction(UIAlertAction(title: "Ok", style: .destructive) { _ in
//                        print("API Error")
//                    })
//                    self.present(alert, animated: true, completion: nil)
//                }
//            }
//        }
//    }
//
//}




import UIKit

struct DoctorImage {
    var doctorImage: UIImage?
}

class Update_doctor: UIViewController, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    
    @IBOutlet weak var profileImage: UIImageView!
    @IBOutlet weak var nameTF: UITextField!
    @IBOutlet weak var emailTF: UITextField!
    @IBOutlet weak var designationTF: UITextField!
    @IBOutlet weak var passwordTF: UITextField!
    
    var id: String?
    var doctor_profile: DoctorProfileModel?
    var selectedImages: [DoctorImage] = []
    let imagePicker = UIImagePickerController()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupImagePicker()
        setupGestureRecognizers()
        postAPI()
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
    }
    @IBAction func back(_ sender: Any) {
            self.navigationController?.popViewController(animated: true)
            for controller in self.navigationController!.viewControllers as Array {
                if controller.isKind(of: Doctor_Profile.self) {
                    self.navigationController!.popToViewController(controller, animated: true)
                    break
                }
            }
        }
    
    func setupImagePicker() {
        imagePicker.delegate = self
        imagePicker.allowsEditing = true
    }
    
    func setupGestureRecognizers() {
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(dismissKeyboard))
        self.view.addGestureRecognizer(tapGesture)
        
        let imageTapGesture = UITapGestureRecognizer(target: self, action: #selector(profileImageTapped))
        profileImage.isUserInteractionEnabled = true
        profileImage.addGestureRecognizer(imageTapGesture)
    }
    
    @objc func dismissKeyboard() {
        view.endEditing(true)
    }
    
    @objc func profileImageTapped() {
        presentImagePicker()
    }
    
    func presentImagePicker() {
        let alert = UIAlertController(title: "Choose Image", message: nil, preferredStyle: .actionSheet)
        alert.addAction(UIAlertAction(title: "Camera", style: .default, handler: { _ in
            self.openCamera()
        }))
        alert.addAction(UIAlertAction(title: "Gallery", style: .default, handler: { _ in
            self.openGallery()
        }))
        alert.addAction(UIAlertAction(title: "Cancel", style: .cancel, handler: nil))
        
        if let popoverController = alert.popoverPresentationController {
            popoverController.sourceView = self.profileImage
            popoverController.sourceRect = self.profileImage.bounds
        }
        present(alert, animated: true, completion: nil)
    }
    
    func openCamera() {
        if UIImagePickerController.isSourceTypeAvailable(.camera) {
            imagePicker.sourceType = .camera
            present(imagePicker, animated: true, completion: nil)
        } else {
            print("Camera not available")
        }
    }
    
    func openGallery() {
        imagePicker.sourceType = .photoLibrary
        present(imagePicker, animated: true, completion: nil)
    }
    
    func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        if let pickedImage = info[UIImagePickerController.InfoKey.editedImage] as? UIImage {
            profileImage.image = pickedImage
            updateSelectedImages(with: pickedImage)
        }
        picker.dismiss(animated: true, completion: nil)
    }
    
    func updateSelectedImages(with image: UIImage) {
        selectedImages = [DoctorImage(doctorImage: image)]
    }
    
    @IBAction func save(_ sender: Any) {
        GettAPI()
        navigateToDoctorProfile()
    }
    
    func navigateToDoctorProfile() {
        let storyBoard = UIStoryboard(name: "Main", bundle: nil)
        let vc = storyBoard.instantiateViewController(withIdentifier: "Doctor_Profile") as! Doctor_Profile
        navigationController?.pushViewController(vc, animated: true)
    }
    
    func postAPI() {
            let apiURL = APIList.DoctorProfileApi
            let parameters: [String: String] = [:] // Adjust according to the API requirements

            APIHandler().postAPIValues(type: DoctorProfileModel.self, apiUrl: apiURL, method: "POST", formData: parameters) { result in
                switch result {
                case .success(let data):
                    self.doctor_profile = data
                    DispatchQueue.main.async {
                        self.updateUI(with: data)
                    }
                case .failure(let error):
                    DispatchQueue.main.async {
                        self.presentAPIError(error: error)
                    }
                }
            }
        }

        func updateUI(with profile: DoctorProfileModel) {
            nameTF.text = profile.name
            emailTF.text = profile.email
            passwordTF.text = profile.password
            designationTF.text = profile.designation
            if let imageString = profile.image, let imageData = Data(base64Encoded: imageString) {
                    profileImage.image = UIImage(data: imageData)
                }
        }

        func presentAPIError(error: Error) {
            let alert = UIAlertController(title: "Warning", message: "Something went wrong: \(error.localizedDescription)", preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "OK", style: .destructive))
            self.present(alert, animated: true, completion: nil)
        }
    func GettAPI() {
           let apiURL = APIList.DoctorProfileUpdateApi
           print(apiURL)
           let boundary = UUID().uuidString
           var request = URLRequest(url: URL(string: apiURL)!)
           request.httpMethod = "POST"
           request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
           var body = Data()

           let formData: [String: String] = [
               "name": "\(nameTF.text ?? "name")",
               "email": "\(emailTF.text ?? "email")",
               "password": "\(passwordTF.text ?? "password")",
               "designation": "\(designationTF.text ?? "designation")"
           ]

           for (key, value) in formData {
               body.append(contentsOf: "--\(boundary)\r\n".utf8)
               body.append(contentsOf: "Content-Disposition: form-data; name=\"\(key)\"\r\n\r\n".utf8)
               body.append(contentsOf: "\(value)\r\n".utf8)
           }

           // Append image data for the selected button index
           for (index, imageData) in selectedImages.enumerated() {
               if let image = getImage(from: imageData), let imageData = image.jpegData(compressionQuality: 0.8) {
                   let fieldName: String

                   switch index {
                   case 0:
                       fieldName = "doctor_img"
                   default:
                       continue
                   }
                   print("Image Data Size for \(fieldName):", imageData.count)

                   body.append(contentsOf: "--\(boundary)\r\n".utf8)
                   body.append(contentsOf: "Content-Disposition: form-data; name=\"\(fieldName)\"; filename=\"\(UUID().uuidString).jpg\"\r\n".utf8)
                   body.append(contentsOf: "Content-Type: image/jpeg\r\n\r\n".utf8)
                   body.append(contentsOf: imageData)
                   body.append(contentsOf: "\r\n".utf8)
               }
           }

           // Close the request body
           body.append(contentsOf: "--\(boundary)--\r\n".utf8)
           request.httpBody = body

           let task = URLSession.shared.dataTask(with: request) { (data, response, error) in
               // Handle the response
               if let data = data {
                   print("Response Data:", String(data: data, encoding: .utf8) ?? "")
               }
               if let error = error {
                   print("Error:", error.localizedDescription)
               }
           }
           task.resume()
       }

       func getImage(from imageData: DoctorImage) -> UIImage? {
           if let doctorImage = imageData.doctorImage {
               return doctorImage
           } else {
               return nil
           }
       }
    }


