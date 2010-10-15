package citykids.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;

import easyenterprise.lib.command.Command;
import easyenterprise.lib.command.gwt.GwtCommandFacade;
import easyenterprise.lib.gwt.ui.Header;
import easyenterprise.server.party.Person;
import easyenterprise.server.party.commands.UpdatePartyResult;
import easyenterprise.server.party.commands.UpdatePerson;

public class RelationManagementPage extends Page {

	protected Header header;

	public RelationManagementPage() {
		initWidget(new FlowPanel() {{
			add(new Header(1, "Relaties") {{
				header = this;
			}});
			add(new Button("Persoon aanmaken", new ClickHandler() {
				public void onClick(ClickEvent event) {
					UpdatePerson udpatePerson = new UpdatePerson()
						.setParty(new Person()
							.setFirstName("Ruud")
							.setLastName("Diterwich"));
					GwtCommandFacade.execute(udpatePerson, new AsyncCallback<UpdatePartyResult<Person>>() {
						public void onSuccess(UpdatePartyResult<Person> result) {
							System.out.println("Gelukt");
						}
						public void onFailure(Throwable caught) {
							System.out.println("Niet gelukt: " + caught);
						}
					});
				}
			}));
		}});
	}
}
