
import request from "../utils/request";



export function postPhoto(formData:FormData){
    return request.post("/photographer/post",formData,{
        headers:{
            'Content-Type':'multipart/form-data',
        },
    });
}

export function deleteImage(photographerId:string){
    return request.delete(`/photographer/deleteImage/${photographerId}`,{
        headers:{
            "Content-Type":'application/json'
        }
    })
}
export function getAvatarUrl(avatar:FormData,id:number|string){
    return request.post(`photographer/upload/avatar/${id}`,avatar,{
      
  headers: { 'Content-Type': 'multipart/form-data' }
}
        
    )
}

export function modifyProfile(userProfile:FormData,userId:string|number){
    return request.post(`/profiles/modify/${userId}`,userProfile,{
  headers: { 'Content-Type': 'multipart/form-data' }
}

    )
}