package considium.subsidieplatform.presentation.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import considium.subsidieplatform.presentation.client.widgets.Page;

public class SearchPage extends Page {

	public SearchPage(EntryPoint entryPoint, String historyToken) {
		super(entryPoint, historyToken);
		createContent();
	}
	
	@Override
	public void show() {
		super.show();
		entryPoint.frame.setCenterWidget(this);
	}

	private void createContent() {
		
		FlowPanel rootPanel = new FlowPanel() {{

			add(new VerticalPanel() {{
				setStyleName("SearchBox");
				setHorizontalAlignment(ALIGN_CENTER);
				add(new HorizontalPanel() {{
					add(new Label("Zoekterm:"));
					add(new TextBox());	
				}});
				add(new HorizontalPanel() {{
					add(new VerticalPanel() {{
						setStyleName("Panel");
						add(new Label("thema") {{ setStyleName("PanelHeader"); }});
						add(new CheckBox("Monumenten"));
						add(new CheckBox("Basisonderwijs"));
						add(new CheckBox("Beroepsonderwijs"));
					}});
					add(new VerticalPanel() {{
						setStyleName("Panel");
						add(new Label("bedrijfssector") {{ setStyleName("PanelHeader"); }});
						add(new CheckBox("Handel"));
						add(new CheckBox("Onderwijs"));
						add(new CheckBox("Communicatie"));
					}});
					add(new VerticalPanel() {{
						setStyleName("Panel");
						add(new Label("instantie") {{ setStyleName("PanelHeader"); }});
						add(new CheckBox("SenterNovem"));
						add(new CheckBox("Provincie"));
						add(new CheckBox("Europese Commissie"));
					}});
				}});
				add(new Button("Zoeken"));
			}});
		}};
		
		
		((Panel)getWidget()).add(rootPanel);
	}
	
}
