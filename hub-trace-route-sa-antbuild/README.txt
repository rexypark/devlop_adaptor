hub-trace-route-sa SA를 생성하기위한 ant 빌드파일.

1. 이클립스에서 실행방법:
build-sa.xml 우 클릭후 => Run As =>Ant Build

2. dest폴더 아래에 zip파일 생성

3. 버젼 바꾸는 방법
build-sa.xml의 
<property name="sa.version" value="1.3" />
값을 수정 후 build

4. SA에 같이 묶일 dependency 라이브러리 추가
hub-trace-route-sa\template-hub-trace-route-bean-su\lib 에 jar파일 복사 해 넣고 build
- ESB classpath + SA의 lib/*.jar 이기때문에 hub-trace-route-sa\template-hub-trace-route-bean-su\lib 에는 ESB의 lib폴더에 없는것만 추가함.

5. lib폴더의 jar는 이클립스에서 컴파일을 위한 jar파일들임.
- spring*등의 라이브러리가 더 많음.



