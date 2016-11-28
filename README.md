# LineGraph
Scroll LineGraph

滑动显示标注！代码还是比较凌乱的，之后再重写的设计重构吧！因为是是Demo和思路！所以就没有过多的设置设计模式等等的相关的规范！在本人正式看完自定义View的时候
随之就会将其重构的更加的完整！，所以再这里不在设置了！最后还是看下预览的效果吧！
主要的部分还是在onTouch 事件中！

比如，获取距离的指数：

public int 	getDistanceIndex(final int distance , int[] Values){
		if (distance < Values[0] || distance > Values[Values.length-1]) {
			return -1;
		}
		int index = Values.length -1;
		int start = Values.length / 2;
		int value = Values[start];

		Log.d("YYYY", "索引---"+ index+"  "+"开始  ---"+ start +"数值---- "+value);

		int formerValue = 0, afterValue = 0;
		if(value > distance){
			for (int i = start - 1; i >= 0; i--) {
				value = Values[i];
				if(value < distance){
					index = i;
					break;
				}
			}
			formerValue = value;
			if (index == 7) { // 有时候会出现 角标越界问题，所以在此追加判断
				index = 6;
			}
			afterValue = Values[index + 1];
		}else{
			for (int i = start + 1; i < Values.length; i++) {
				value = Values[i];
				if(value > distance){
					index = i - 1;
					break;
				}
			}
			formerValue = Values[index];
			afterValue = value;
		}

		int formerDiff = distance - formerValue;
		int midelDiff = (afterValue - formerValue) / 2;
		if(formerDiff > midelDiff){
			return index + 1;
		}else{
			return index;
		}
	}
  
  等等的一些相关处理，本Demo的遗憾和缺漏之处如 重绘的次数、和在绘制曲线的阴影部分还需要在完善，目前还没有处理！
  验证思路：1、有可能是 Point 集合中点的值有所变化，或者是不对
             疑问：但是在第一次绘制的时候，没有出现相关的问题！
             
           2、再者就是在 invalidate() 的时候出现相关的绘制错误！
           
    总结:重写和抽取是必须的，所以这是之后的工作！(自定义View 彻底完结之后)!
    
    不喜勿喷！！！！！！
![](https://github.com/wanglyGithub/LineGraph/blob/master/app/src/main/res/preview/test.gif)
