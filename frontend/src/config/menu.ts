 //自定义菜单配置（可扩展）
  const memuConfig={
    common:[
      {
        key:'firstPage',
        label:'首页'
      },
      
    ],
    customer:[
      {
        key:'customer/order',
        label:'订单'
      },
      {
        key:'customer/dashboard',
        label:'个人中心'
      },
    ],
    photographer:[
      {
        key:'photographer/dashboard',
        label:'个人中心'
      },
      {
        key:'photographer/profile',
        label:'上传作品'
      },
      {
        key:'photographer/wallet',
        label:'钱包'
      }
    ]
  }

 export default memuConfig;